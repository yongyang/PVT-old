package org.jboss.pnc.pvt.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;

import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.CallBack;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.ExecutionRunnable;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.report.ReleaseReport;
import org.jboss.pnc.pvt.wicket.PVTApplication;
import org.osgi.framework.Version;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A verify tool to check the version convention, ex: the jars should have -rehdat-x suffix in version
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
@JsonAutoDetect
@JsonSubTypes({ @JsonSubTypes.Type(value = VersionConventionVerifyTool.class) })
public class VersionConventionVerifyTool extends VerifyTool {

    private static final long serialVersionUID = 8002107449547514235L;

    private static final Logger logger = Logger.getLogger(VersionConventionVerifyTool.class);

    /**
     * Default we require versions are OSGI compatible.
     */
    private boolean osgiCompatible = true;

    /**
     * You can also specify the pattern for the version string.
     */
    private String versionPattern;

    /**
     * @return the osgiCompatible
     */
    public boolean isOsgiCompatible() {
        return osgiCompatible;
    }

    /**
     * @param osgiCompatible the osgiCompatible to set
     */
    public void setOsgiCompatible(boolean osgiCompatible) {
        this.osgiCompatible = osgiCompatible;
    }

    /**
     * @return the versionPattern
     */
    public String getVersionPattern() {
        return versionPattern;
    }

    /**
     * @param versionPattern the versionPattern to set
     */
    public void setVersionPattern(String versionPattern) {
        this.versionPattern = versionPattern;
    }

    private boolean isOSGIVersion(String version) {
        try {
            Version.parseVersion(version);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Verification verify(VerifyParameter param) {
        
        final String name = "Version-Check-For-" + param.getRelease().getName();
        final ExecutionRunnable run = verifyRunnable(param);
        final Execution execution = Execution.createJVMExecution(name, run);

        final Verification verification = getOrCreateVerification(param, execution);
        if (Verification.Status.PASSED.equals(verification.getStatus())) {
            // has passed already, should we re start it again?
            if (Boolean.valueOf(param.getProperty(VerifyParameter.SKIP_PASSED, "False"))) {
                return verification;
            }
        }
        try {
            Executor.getJVMExecutor().execute(execution, defaultVerificationCallBack(verification));
        } catch (ExecutionException e) {
            verification.setStatus(Verification.Status.NOT_PASSED);
            logger.error("Failed to execute the Jenkins job: " + execution.getName(), e);
        }
        return verification;
    }

    protected ExecutionRunnable verifyRunnable(final VerifyParameter param) {
        return new ExecutionRunnable() {
            
            @Override
            public void doRun() throws Exception {
                String[] zips = param.getRelease().getDistributionArray();
                Objects.requireNonNull(zips, "Released Archives must be spcified.");
                if (zips.length > 0) {
                    final ReleaseReport releaseReport = new ReleaseReport(getVerifiedProductName(param), param.getReferenceRelease().getName());
                    for (String zip: zips) {
//                        try {
//                            logger.info("Downloading file: " + zip);
//                            File downloadedZip = downloadZip(zip);
//                            logger.info("Downloaded file: " + zip);
//                            try (FileSystem zipFS = FileSystems.newFileSystem(Paths.get(downloadedZip.toURI()), getClass().getClassLoader())) {
//                                Files.walkFileTree(zipFS.getPath("/"), zipJarVistor(releaseReport, downloadedZip, this));
//                            }
//                        } catch (IOException e) {
//                            logger.warn("Can't download zip: " + zip, e);
//                            break;
//                        }
                    }
                }
            }

        };
    }

    private String getVerifiedProductName(VerifyParameter param) {
        String prdId = param.getRelease().getProductId();
        Product product = PVTApplication.getDAO().getPvtModel().getProductbyId(prdId);
        return product.getName();
    }

    // maybe set up a local cache for performance ? download only the md5 sum is different!?
    private File downloadZip(String zipURL) throws IOException {
        Path zipFile = Paths.get(createTempDownloadedDir(), getZipFileName(zipURL));
        Files.copy(new URL(zipURL).openStream(), zipFile, StandardCopyOption.REPLACE_EXISTING);
        return zipFile.toFile();
    }

    /** The zip file name should be the last part in the URL **/
    private String getZipFileName(String zipURL) {
        return zipURL.substring(zipURL.lastIndexOf("/"));
    }

    private String createTempDownloadedDir() throws IOException {
        String tmpDirRoot = System.getProperty("pvt.tmp.download.dir", System.getProperty("jboss.server.temp.dir", System.getenv("java.io.tmpdir")));
        Path tmpDir = Files.createTempDirectory(Paths.get(tmpDirRoot, "pvt"), "downloads");
        return tmpDir.toString();
    }

    protected SimpleFileVisitor<Path> zipJarVistor(final ReleaseReport releaseReport, final File zip, final ExecutionRunnable execRun) {
        return new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path jar, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile() && jar.endsWith(".jar")) {
                    // OK, it is a jar file
                    addJarReport(jar, releaseReport);
                    JsonGenerator d = null;
//                    execRun.getExecution().setLog(new ObjectMapper().wr)
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

    protected void addJarReport(Path jar, ReleaseReport releaseReport) {
        // TODO Auto-generated method stub
        
    }
}