package org.jboss.pnc.pvt.model;

import jdk.internal.util.xml.impl.Input;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class JDKCompatibleVerifyTool extends VerifyTool {

    private static final int JAVA_CLASS_MAGIC = 0xCAFEBABE;

    private String expectJDKVersion;

    @Override
    protected VerifyResult verify(VerifyParameter param) {
        String zipUrl = param.getCurrentDistributionZip();
        boolean passed = true;
        try(ZipInputStream zipInputStream = new ZipInputStream(new URL(zipUrl).openStream())) {

            File zip = File.createTempFile(zipUrl, "zip");
            IOUtils.copy(new URL(zipUrl).openStream(), new FileWriter(zip));

            ZipFile zipFile = new ZipFile(zip);
            for( Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); ) {
                ZipEntry zipEntry = entries.nextElement();
                if(zipEntry.getName().endsWith(".jar")) {
                    JarEntry jarEntry = new JarEntry(zipEntry);
                    InputStream in = zipFile.getInputStream(jarEntry);
//                    IOUtils.copy()
                    //TODO: read class from each jars
                    if(!checkClassVersion(in, expectJDKVersion)) {
                        passed =false;
                        break;
                    }
                }


            }


            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while(zipEntry != null) {
                if(zipEntry.getName().endsWith(".jar")) {
                    JarEntry jarEntry = new JarEntry(zipEntry);
                    //TODO: read class from each jars
                    if(!checkClassVersion(null, expectJDKVersion)) {
                        passed =false;
                        break;
                    }
                }
                zipEntry = zipInputStream.getNextEntry();
            }

            final boolean p = passed;
            VerifyResult<Boolean> verifyResult = new VerifyResult<Boolean>() {
                @Override
                public Boolean getResult() {
                    //TODO: return the detail info
                    return p;
                }
            };
            verifyResult.setStatus(p ? VerifyResult.Status.PASSED : VerifyResult.Status.REJECTED);
            return verifyResult;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkClassVersion(InputStream classInputStream, String expectJDKVersion) throws Exception {
        try {
            DataInputStream dis = new DataInputStream(classInputStream);
            int magic = dis.readInt();
            if(magic == JAVA_CLASS_MAGIC){
                int minorVersion = dis.readUnsignedShort();
                int majorVersion = dis.readUnsignedShort();

//      Java 1.2 uses major version 46
//      Java 1.3 uses major version 47
//      Java 1.4 uses major version 48
//      Java 5 uses major version 49
//      Java 6 uses major version 50
//      Java 7 uses major version 51

                System.out.println("ClassVersionTest.main() " + majorVersion + "." + minorVersion);

                return ("" + majorVersion).equalsIgnoreCase(expectJDKVersion);
            }

            return false;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Unpack matching files from a jar. Entries inside the jar that do not match the given pattern will be skipped.
     * @param jarFile the .jar file to unpack
     * @param toDir the destination directory into which to unpack the jar
     */
    public static void unJar(File jarFile,File toDir) throws IOException {
        JarFile jar = new JarFile(jarFile);
        try {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement();
                if (!entry.isDirectory()) {
                    InputStream in = jar.getInputStream(entry);
                    try {
                        File file = new File(toDir, entry.getName());
//                        ensureDirectory(file.getParentFile());
                        OutputStream out = new FileOutputStream(file);
                        try {
//                            IOUtils.copyBytes(in,out,8192,false);
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
}
