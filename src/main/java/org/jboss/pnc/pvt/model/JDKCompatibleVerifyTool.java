package org.jboss.pnc.pvt.model;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.ExecutionRunnable;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.report.ReleaseReport;
import org.jboss.pnc.pvt.report.ReleaseReport.ZipReport;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
@JsonAutoDetect
@JsonSubTypes({ @JsonSubTypes.Type(value = JDKCompatibleVerifyTool.class) })
public class JDKCompatibleVerifyTool extends AbstractZipAnalysisTool {

    private static final long serialVersionUID = -7513802473705616180L;

    private static final Logger logger = Logger.getLogger(JDKCompatibleVerifyTool.class);

    private static final int JAVA_CLASS_MAGIC = 0xCAFEBABE;

    public enum JDK {

        JDK1_1(45, 3), JDK1_2(46, 0), JDK1_3(47, 0), JDK1_4(48, 0), JDK5(49, 0), JDK6(50, 0), JDK7(51, 0), JDK8(52, 0);

        private final int major;
        private final int minor;

        private JDK(int major, int minor) {
            this.major = major;
            this.minor = minor;
        }

        /**
         * @return the major
         */
        public int getMajor() {
            return major;
        }

        /**
         * @return the minor
         */
        public int getMinor() {
            return minor;
        }

        public static JDK getJDK(int major, int minor) {
            for (JDK jdk : JDK.values()) {
                if (jdk.getMajor() == major && jdk.getMinor() == minor) {
                    return jdk;
                }
            }
            return null;
        }
    }

    private JDK minJDK = JDK.JDK1_2;

    private JDK maxJDK = JDK.JDK8;

    /**
     * Whether returns immediately after the first class analysis.
     */
    private boolean fastReturn = true;

    /**
     * @return the minJDK
     */
    public JDK getMinJDK() {
        return minJDK;
    }

    /**
     * @param minJDK the minJDK to set
     */
    public void setMinJDK(JDK minJDK) {
        this.minJDK = minJDK;
    }

    /**
     * @return the maxJDK
     */
    public JDK getMaxJDK() {
        return maxJDK;
    }

    /**
     * @param maxJDK the maxJDK to set
     */
    public void setMaxJDK(JDK maxJDK) {
        this.maxJDK = maxJDK;
    }

    /**
     * @return the fastReturn
     */
    public boolean isFastReturn() {
        return fastReturn;
    }

    /**
     * @param fastReturn the fastReturn to set
     */
    public void setFastReturn(boolean fastReturn) {
        this.fastReturn = fastReturn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "JDKCompatibleVerifyTool " + "[name=" + getName() + ", minJDK=" + minJDK + ", maxJDK=" + maxJDK + ", fastReturn=" + fastReturn + "]";
    }

    @Override
    public Verification verify(VerifyParameter param) {

        final String proudctName = getProductName(param.getRelease().getProductId());
        final String name = "JDK-Check-For-" + proudctName + "-" + param.getRelease().getName();
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

    private JDK readClassTargetJDK(InputStream input) throws IOException {
        DataInputStream dis = new DataInputStream(input);
        int magic = dis.readInt();
        if (magic != JAVA_CLASS_MAGIC) {
            throw new IOException("Invalid magic number for java class file.");
        }
        int minor = dis.readUnsignedShort();
        int major = dis.readUnsignedShort();
        logger.debug("Major: " + major + ", Minor: " + minor);
        return JDK.getJDK(major, minor);
    }

    private boolean isJDKOK(JDK jdk) {
        if (jdk.compareTo(getMinJDK()) < 0) {
            return false;
        }
        if (jdk.compareTo(getMaxJDK()) > 0) {
            return false;
        }
        return true;
    }

    @Override
    protected void walkJarFile(Path zipPath, Path jarPath, ReleaseReport releaseReport, ZipReport zipReport) throws IOException {
        final JDKVersionJarReport jarReport = new JDKVersionJarReport(jarPath.getFileName().toString());
        zipReport.addJarReport(jarReport);
        Path jarCopy = Files.copy(jarPath, Paths.get(zipPath.getParent().toString(), jarPath.getFileName().toString()),
                StandardCopyOption.REPLACE_EXISTING);
        logger.debug("Jar: " + jarPath.toString() + " is copied to: " + jarCopy.toString());
        try (FileSystem jarFS = FileSystems.newFileSystem(jarCopy, getClass().getClassLoader())) {
            Files.walkFileTree(jarFS.getPath("/"), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".class")) {
                        try (InputStream input = Files.newInputStream(file, StandardOpenOption.READ)) {
                            JDK jdk = readClassTargetJDK(input);
                            if (jdk == null) {
                                throw new IOException("Unknown JDK version of class: " + file.toString());
                            }
                            jarReport.setJdk(jdk);
                            jarReport.setValid(isJDKOK(jdk));
                            if (isFastReturn()) {
                                return FileVisitResult.TERMINATE;
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

            });

        } finally {
            if (!jarCopy.toFile().delete()) {
                jarCopy.toFile().deleteOnExit();
            }
        }
    }

    class JDKVersionJarReport extends ReleaseReport.JarReport {

        private JDK jdk;

        private boolean valid;

        public JDKVersionJarReport(String jarName) {
            super(jarName);
        }

        /**
         * @return the jdk
         */
        public JDK getJdk() {
            return jdk;
        }

        /**
         * @param jdk the jdk to set
         */
        public void setJdk(JDK jdk) {
            this.jdk = jdk;
        }

        @JsonProperty
        public String expectedJDK() {
            return "[" + getMinJDK().toString() + " - " + getMaxJDK().toString() + "]";
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

        @Override
        public boolean overAll() {
            return isValid();
        }

    }
}
