package org.jboss.pnc.pvt.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.ExecutionRunnable;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.report.ReleaseReport;
import org.jboss.pnc.pvt.wicket.PVTApplication;
import org.osgi.framework.Version;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;

/**
 * A verify tool to check the version convention, ex: the jars should have -rehdat-x suffix in version
 *
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
@JsonAutoDetect
@JsonSubTypes({ @JsonSubTypes.Type(value = VersionConventionVerifyTool.class) })
public class VersionConventionVerifyTool extends AbstractZipAnalysisTool {

    private static final long serialVersionUID = 8002107449547514235L;

    private static final Logger logger = Logger.getLogger(VersionConventionVerifyTool.class);

    public static final String DEFAULT_REDHAT_PATTERN = ".+[\\.|-]redhat-\\d+$";

    /**
     * Default we require versions are OSGI compatible.
     */
    private boolean checkOSGI = true;

    /**
     * You can also specify the pattern for the version string.
     */
    private String versionPattern = DEFAULT_REDHAT_PATTERN;

    /**
     * @return the checkOSGI
     */
    public boolean isCheckOSGI() {
        return checkOSGI;
    }

    /**
     * @param checkOSGI the checkOSGI to set
     */
    public void setCheckOSGI(boolean checkOSGI) {
        this.checkOSGI = checkOSGI;
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

    protected void walkJarFile(final Path zipPath, final Path jar, ReleaseReport releaseReport, final ReleaseReport.ZipReport zipReport)
            throws IOException {
        VersionJarReport jarReport = new VersionJarReport(jar.getFileName().toString());
        zipReport.addJarReport(jarReport);
        Path jarCopy = Files.copy(jar, Paths.get(zipPath.getParent().toString(), jar.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
        logger.debug("Jar: " + jar.toString() + " is copied to: " + jarCopy.toString());
        try (FileSystem jarFS = FileSystems.newFileSystem(jarCopy, getClass().getClassLoader())) {
            Files.walkFileTree(jarFS.getPath("/"), new SimpleFileVisitor<Path>() {

                private String preferVersion = null;

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith("pom.properties")) {
                        if (this.preferVersion == null) {
                            try (InputStream input = Files.newInputStream(file, StandardOpenOption.READ)) {
                                Properties props = new Properties();
                                props.load(input);
                                String v = props.getProperty("version");
                                jarReport.setVersion(v);
                                String failMsg = isValid(v);
                                jarReport.setFailReason(failMsg);
                                jarReport.setValid(failMsg == null);

                                String artiId = props.getProperty("artifactId");
                                if (jar.getFileName().toString().contains(artiId)) {
                                    preferVersion = v;
                                }
                            }
                        }
                    } else if (file.toString().endsWith("META-INF/MANIFEST.MF")) {
                        if (this.preferVersion == null) {
                            List<String> lines = Files.readAllLines(file);
                            String key = "Implementation-Version:";
                            for (String line: lines) {
                                if (line.startsWith(key)) {
                                    String version = line.substring(key.length());
                                    jarReport.setVersion(version);
                                    String failMsg = isValid(version);
                                    jarReport.setFailReason(failMsg);
                                    jarReport.setValid(failMsg == null);
                                }
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    logger.warn("Failed to read File: " + file.toString() + ", skip sub tree files under it.");
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
            
        } finally {
            if (!jarCopy.toFile().delete()) {
                jarCopy.toFile().deleteOnExit();
            }
        }
    }

    protected String isValid(String version) {
        if (version == null) {
            return "Version is unknown.";
        }
        if (isCheckOSGI()) { // check OSGI
            if (!isOSGIVersion(version)) {
                return String.format("%s is not OSGI compatible", version);
            }
        }
        if (getVersionPattern() != null && getVersionPattern().trim().length() > 0) { // check version pattern
            if (!version.matches(getVersionPattern())) {
                return String.format("%s does not match the pattern: %s", version, getVersionPattern());
            }
        }
        return null;
    }

    @JsonInclude(Include.NON_NULL)
    static class VersionJarReport extends ReleaseReport.JarReport {

        private String version;
        private boolean valid;
        private String failReason;

        /* (non-Javadoc)
         * @see org.jboss.pnc.pvt.report.PlainReport#overAll()
         */
        @Override
        public boolean overAll() {
            return isValid();
        }

        public VersionJarReport(String jarName) {
            super(jarName);
        }

        /**
         * @return the failReason
         */
        public String getFailReason() {
            return failReason;
        }

        /**
         * @param failReason the failReason to set
         */
        public void setFailReason(String failReason) {
            this.failReason = failReason;
        }

        /**
         * @return the version
         */
        public String getVersion() {
            return version;
        }

        /**
         * @param version the version to set
         */
        public void setVersion(String version) {
            this.version = version;
        }

        /**
         * @return the valid
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * @param valid the valid to set
         */
        public void setValid(boolean valid) {
            this.valid = valid;
        }

    }
}