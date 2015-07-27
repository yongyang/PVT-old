package pvt.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.pnc.pvt.model.PVTModel;
import org.jboss.pnc.pvt.model.Product;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */
public class JSONTest {

    public static void main(String[] args) throws Exception {
        PVTModel pvtModel = new PVTModel();
        Product product = new Product();
        product.setName("testName");
        product.setDescription("testDesc");
        product.setContactor("test@test.com");
        product.setMaintainer("test@test.com");

        pvtModel.addProduct(product);

        ObjectMapper map = new ObjectMapper();
        map.writeValue(System.out, pvtModel);
//        System.out.println(json);
    }
}
