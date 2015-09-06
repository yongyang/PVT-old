/*
 * JBoss, Home of Professional Open Source
 * Copyright @year, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.pnc.pvt.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionRunnable;
import org.jboss.pnc.pvt.report.ReleaseReport;

/**
 * @author <a href="mailto:lgao@redhat.com">Lin Gao</a>
 *
 */
public abstract class AbstractZipAnalysisTool extends VerifyTool {

    private static final long serialVersionUID = 7083076878352456670L;

    private static final Logger logger = Logger.getLogger(AbstractZipAnalysisTool.class);
    
    protected ExecutionRunnable verifyRunnable(final VerifyParameter param) {
        return new ExecutionRunnable() {

            @Override
            public void doRun() throws Exception {
                String[] remoteZipURLs = param.getRelease().getDistributionArray();
                Objects.requireNonNull(remoteZipURLs, "Released Archives must be spcified.");
                if (remoteZipURLs.length > 0) {
                    final ReleaseReport releaseReport = new ReleaseReport(getVerifiedProductName(param), param
                            .getRelease().getName());
                    for (String remoteZip : remoteZipURLs) {
                        URL remoteZipURL = new URL(remoteZip);
                        logger.debug("Downloading file: " + remoteZipURL);
                        releaseReport.setStatusMsg("Downloading file: " + remoteZipURL);
                        File downloadedZip = downloadZip(remoteZipURL);
                        logger.debug("Downloaded file: " + remoteZipURL);
                        releaseReport.setStatusMsg("Downloaded file: " + remoteZipURL);
                        ReleaseReport.ZipReport zipReport = new ReleaseReport.ZipReport(downloadedZip.getName());
                        releaseReport.addZipReport(zipReport);
                        Path zipPath = Paths.get(downloadedZip.toURI());
                        try (FileSystem zipFS = FileSystems.newFileSystem(zipPath, getClass()
                                .getClassLoader())) {
                            Files.walkFileTree(zipFS.getPath("/"), walkZipFile(zipPath, releaseReport, zipReport, downloadedZip, this));
                        } finally {
                            if (!downloadedZip.delete()) {
                                downloadedZip.deleteOnExit();
                            }
                            if (!downloadedZip.getParentFile().delete()) {
                                downloadedZip.getParentFile().deleteOnExit();
                            }
                        }
                    }
                    releaseReport.setStatusMsg("COMPLETED");
                    setLog(releaseReport.toJSONString());
                    if (releaseReport.overAll()) {
                        setStatus(Execution.Status.SUCCEEDED);
                    } else {
                        setStatus(Execution.Status.FAILED);
                    }
                }
            }
        };
    }

    // maybe set up a local cache for performance ? download only the md5 sum is different!?
    private File downloadZip(URL remoteZipURL) throws IOException, URISyntaxException {
        Path zipFile = Paths.get(createTempDownloadedDir(), getFileName(remoteZipURL));
        logger.debug("Downloading : " + remoteZipURL + " to: " + zipFile.toAbsolutePath().toString());
        Files.copy(remoteZipURL.openStream(), zipFile, StandardCopyOption.REPLACE_EXISTING);
        return zipFile.toFile();
    }

    private String getFileName(URL remoteZipURL) {
        String path = remoteZipURL.getPath();
        return path.substring(path.lastIndexOf("/"));
    }

    private String createTempDownloadedDir() throws IOException {
        String tmpDirRoot = System.getProperty("pvt.tmp.download.dir",
                System.getProperty("jboss.server.temp.dir", System.getProperty("java.io.tmpdir", "/tmp")));
        Path dir = Paths.get(tmpDirRoot, "pvt");
        Files.createDirectories(dir);
        Path tmpDir = Files.createTempDirectory(dir, "downloads-");
        return tmpDir.toString();
    }

    protected SimpleFileVisitor<Path> walkZipFile(final Path zipPath, final ReleaseReport releaseReport, final ReleaseReport.ZipReport zipReport,
            final File zip, final ExecutionRunnable execRun) throws IOException {
        return new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path jar, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile() && jar.toString().endsWith(".jar")) {
                    // OK, it is a jar file
                    logger.debug("Analizing jar: " + jar.toString());
                    releaseReport.setStatusMsg("Analizing jar: " + jar.toString());
                    walkJarFile(zipPath, jar, releaseReport, zipReport);
                    // update log after each jar analysis
                    execRun.setLog(releaseReport.toJSONString());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                logger.warn("Failed to read File: " + file.toString() + ", skip sub tree files under it.");
                return FileVisitResult.SKIP_SUBTREE;
            }
        };
    }

    /**
     * Walk through the Jar file.
     * 
     * Typically, it will create a JarReport, then add it to the ZipReport.
     * 
     * @param zipPath the zip file which the jar file belongs to.
     * @param jarPath the jar file which will be analysis in this method.
     * @param releaseReport the main ReleaseReport
     * @param zipReport the zipReport
     * @throws IOException on any I/O Exception
     */
    protected abstract void walkJarFile(Path zipPath, Path jarPath, ReleaseReport releaseReport, ReleaseReport.ZipReport zipReport) throws IOException;

}