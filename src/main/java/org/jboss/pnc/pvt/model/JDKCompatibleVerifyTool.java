package org.jboss.pnc.pvt.model;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jboss.logging.Logger;
import org.jboss.pnc.pvt.execution.CallBack;
import org.jboss.pnc.pvt.execution.Execution;
import org.jboss.pnc.pvt.execution.ExecutionException;
import org.jboss.pnc.pvt.execution.ExecutionRunnable;
import org.jboss.pnc.pvt.execution.Executor;
import org.jboss.pnc.pvt.report.ReleaseReport;
import org.jboss.pnc.pvt.report.ReleaseReport.ZipReport;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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

    private String expectJDKVersion;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [name=" + getName() + ", expectJDKVersion=" + getExpectJDKVersion() + "]";
    }

    @Override
    public Verification verify(VerifyParameter param) {

        final String proudctName = getProductName(param.getRelease().getProductId());
        final String name = "JDK-Check-For-" + proudctName + "-" + param.getRelease().getName();
        final ExecutionRunnable run = jdkCheckRunnable(param);
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

        // TODO: do verify
        // return newDefaultVerification(param, true);
        /*
         * if (getExpectJDKVersion() == null || getExpectJDKVersion().trim().length() == 0) { throw new
         * IllegalStateException("Please set expect JDK version first!"); } String[] zipUrls =
         * param.getRelease().getDistributionArray();
         * 
         * //TODO: foreach zipUrls String zipUrl = zipUrls[0];
         * 
         * boolean passed = true; try(ZipInputStream zipInputStream = new ZipInputStream(new URL(zipUrl).openStream())) {
         * 
         * File zip = File.createTempFile(zipUrl, "zip"); IOUtils.copy(new URL(zipUrl).openStream(), new FileWriter(zip));
         * 
         * ZipFile zipFile = new ZipFile(zip); for( Enumeration<? extends ZipEntry> entries = zipFile.entries();
         * entries.hasMoreElements(); ) { ZipEntry zipEntry = entries.nextElement(); if(zipEntry.getName().endsWith(".jar")) {
         * JarEntry jarEntry = new JarEntry(zipEntry); InputStream in = zipFile.getInputStream(jarEntry); // IOUtils.copy()
         * //TODO: read class from each jars if(!checkClassVersion(in, expectJDKVersion)) { passed =false; break; } }
         * 
         * 
         * }
         * 
         * 
         * ZipEntry zipEntry = zipInputStream.getNextEntry(); while(zipEntry != null) { if(zipEntry.getName().endsWith(".jar"))
         * { JarEntry jarEntry = new JarEntry(zipEntry); //TODO: read class from each jars if(!checkClassVersion(null,
         * expectJDKVersion)) { passed =false; break; } } zipEntry = zipInputStream.getNextEntry(); }
         * 
         * final boolean p = passed; Verification<Boolean> verification = new Verification<Boolean>(param.getToolId(),
         * param.getReferenceRelease()!= null ? param.getReferenceRelease().getId() : "", param.getRelease().getId()) {
         * 
         * @Override public Boolean getResultObject() { //TODO: return the detail info return p; } }; verification.setStatus(p ?
         * Verification.Status.PASSED : Verification.Status.REJECTED); return verification; } catch (Exception e) {
         * e.printStackTrace(); } return null;
         */
    }

    private ExecutionRunnable jdkCheckRunnable(VerifyParameter param) {
        return new ExecutionRunnable() {

            @Override
            public void doRun() throws Exception {
                // TODO Auto-generated method stub
                System.err.println("JDK CHECKING...... ");
            }

        };
    }

    public static boolean checkClassVersion(InputStream classInputStream, String expectJDKVersion) throws Exception {
        try {
            DataInputStream dis = new DataInputStream(classInputStream);
            int magic = dis.readInt();
            if (magic == JAVA_CLASS_MAGIC) {
                int minorVersion = dis.readUnsignedShort();
                int majorVersion = dis.readUnsignedShort();

                // Java 1.2 uses major version 46
                // Java 1.3 uses major version 47
                // Java 1.4 uses major version 48
                // Java 5 uses major version 49
                // Java 6 uses major version 50
                // Java 7 uses major version 51

                System.out.println("ClassVersionTest.main() " + majorVersion + "." + minorVersion);

                return ("" + majorVersion).equalsIgnoreCase(expectJDKVersion);
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Unpack matching files from a jar. Entries inside the jar that do not match the given pattern will be skipped.
     * 
     * @param jarFile the .jar file to unpack
     * @param toDir the destination directory into which to unpack the jar
     */
    public static void unJar(File jarFile, File toDir) throws IOException {
        JarFile jar = new JarFile(jarFile);
        try {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement();
                if (!entry.isDirectory()) {
                    InputStream in = jar.getInputStream(entry);
                    try {
                        File file = new File(toDir, entry.getName());
                        // ensureDirectory(file.getParentFile());
                        OutputStream out = new FileOutputStream(file);
                        try {
                            // IOUtils.copyBytes(in,out,8192,false);
                        } finally {
                            out.close();
                        }
                    } finally {
                        in.close();
                    }
                }
            }
        } finally {
            jar.close();
        }
    }

    /**
     * @return the expectJDKVersion
     */
    public String getExpectJDKVersion() {
        return expectJDKVersion;
    }

    /**
     * @param expectJDKVersion the expectJDKVersion to set
     */
    public void setExpectJDKVersion(String expectJDKVersion) {
        this.expectJDKVersion = expectJDKVersion;
    }

    @Override
    protected void walkJarFile(Path zipPath, Path jarPath, ReleaseReport releaseReport, ZipReport zipReport) throws IOException {
        // TODO Auto-generated method stub
        
    }

}
