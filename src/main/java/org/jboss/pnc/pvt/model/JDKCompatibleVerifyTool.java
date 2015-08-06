package org.jboss.pnc.pvt.model;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
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
                public Boolean getResultMap() {
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
}
