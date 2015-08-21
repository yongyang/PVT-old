package org.jboss.pnc.pvt.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */

@JsonAutoDetect
public class PVTModel implements Serializable {

    private List<Product> products = new ArrayList<>();

    private List<Release> releases = new ArrayList<>();

	private List<VerifyToolType> toolTypes = new ArrayList<>();

	private List<VerifyTool> tools = new ArrayList<>();

	private Map<String,Verification<?>> verifications = new HashMap<>();

    public void addProduct(Product product) {
        products.add(product);
    }
    
    public void updateProduct(Product product) {
    	
    	for (Product p : products){
    		if (p.getId().equals(product.getId())){
    			int index = products.indexOf(p);
    			products.set(index, product);
    			break;	
    		}	
    	}
    }
    
    public void removeProduct(Product product){
    	products.remove(product);
    }
    
    public Product getProductbyId(String id){
    	Product product = new Product();
    	for (Product p : products){
    		if (p.getId().equals(id)){
    			product = p;
    			break;	
    		}	
    	}
    	return product;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addRelease(Release release) {
        releases.add(release);
    }
    
    public void updateRelease(Release release) {
    	
    	for (Release r : releases){
    		if (r.getId().equals(release.getId())){
    			int index = releases.indexOf(r);
    			releases.set(index, release);
    			break;	
    		}	
    	}
    }
    
    public void removeRelease(Release release){
    	releases.remove(release);
    }
    
    public Release getReleasebyId(String id){
    	Release release = new Release();
    	for (Release p : releases){
    		if (p.getId().equals(id)){
    			release = p;
    			break;	
    		}	
    	}
    	return release;
    }

    public List<Release> getReleases() {
        return releases;
    }

	public List<VerifyToolType> getToolTypes() {
		return toolTypes;
	}

	public List<VerifyTool> getTools() {
		return tools;
	}

	public void addTool(VerifyTool tool) {
		tools.add(tool);
	}

	public Map<String, Verification<?>> getVerifications() {
		return verifications;
	}

	public Verification<?> getVerificationById(String id) {
		return verifications.get(id);
	}


}
