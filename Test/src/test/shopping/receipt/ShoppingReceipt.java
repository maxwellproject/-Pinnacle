package test.shopping.receipt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.lang.System;
import java.math.BigDecimal;

import test.shopping.receipt.model.Location;
import test.shopping.receipt.model.Product;
import test.shopping.receipt.model.Purchase;
import test.shopping.receipt.model.PurchaseOutput;

public class ShoppingReceipt {
	public static String CONFIG_PROPERTIES = "\\src\\resources\\config.properties";
	public static String inputFilePath = "\\src\\resources\\usercases.txt";
	
	public static String currentSysPath = System.getProperty("user.dir");
	
	public static String salesTaxRateCA = "";
	public static String salesTaxRateNY = "";
	public static String prodCateExemptCA = "";
	public static String prodCateExemptNY = "";
	public static String prodCateFood = "";
	public static String prodCateCloth = "";
	public static String prodCateOther = "";
	
	public static HashSet<String> exemptCASet = new HashSet<String>();
	public static HashSet<String> exemptNYSet = new HashSet<String>();
	public static HashSet<String> cateFoodSet = new HashSet<String>();
	public static HashSet<String> cateClothSet = new HashSet<String>();
	public static HashSet<String> cateOtherSet = new HashSet<String>();
	
	public void loadProperties() { // config.properties
		System.out.println("loadProperties start");
		String propPath = currentSysPath + CONFIG_PROPERTIES;
		System.out.println(propPath);
		try {
			InputStream input = new FileInputStream(propPath);
			Properties prop = new Properties();
			prop.load(input);
			salesTaxRateCA = prop.getProperty("sales.tax.rate.CA");
			salesTaxRateNY = prop.getProperty("sales.tax.rate.NY");
			prodCateExemptCA = prop.getProperty("product.categories.exempt.CA");
			String[] exemptCAs = prodCateExemptCA.split(",");
			if (exemptCAs != null && exemptCAs.length > 0) {
				for (String eachCA : exemptCAs) {
					exemptCASet.add(eachCA.trim());
				}
			}
			prodCateExemptNY = prop.getProperty("product.categories.exempt.NY");
			String[] exemptNYs = prodCateExemptNY.split(",");
			if (exemptNYs != null && exemptNYs.length > 0) {
				for (String eachNY : exemptNYs) {
					exemptNYSet.add(eachNY.trim());
				}
			}
			prodCateFood = prop.getProperty("product.categories.food");
			String[] cateFoods = prodCateFood.split(",");
			if (cateFoods != null && cateFoods.length > 0) {
				for (String eachCateFood : cateFoods) {
					cateFoodSet.add(eachCateFood.trim());
				}
			}
			prodCateCloth = prop.getProperty("product.categories.clothing");
			String[] prodCateCloths = prodCateCloth.split(",");
			if (prodCateCloths != null && prodCateCloths.length > 0) {
				for (String eachProdCateCloth : prodCateCloths) {
					cateClothSet.add(eachProdCateCloth.trim());
				}
			}
			prodCateOther = prop.getProperty("product.categories.other");
			String[] prodCateOthers = prodCateOther.split(",");
			if (prodCateOthers != null && prodCateOthers.length > 0) {
				for (String eachProdCateOther : prodCateOthers) {
					cateOtherSet.add(eachProdCateOther.trim());
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("loadProperties end");
	}
	
	public LinkedList<String> readInput(String path) { // "D:\\WorkspaceCCB20200124\\Test\\src\\resources\\usercases.txt"
		System.out.println("readInput start");
		File file = new File(path);
		BufferedReader br;
		LinkedList<String> input = new LinkedList<String>();
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				input.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("readInput end");
		return input;
	}
	public LinkedList<Purchase> parseInput(LinkedList<String> input) {
		System.out.println("parseInput start");
		LinkedList<Purchase> purchases = new LinkedList<Purchase>();
		
		for (String each : input) {
			String[] eachSplits = each.split(",");
			if (eachSplits != null && eachSplits.length > 0) {
				Purchase eachPurchase = new Purchase();
				LinkedList<Product> products = new LinkedList<Product>();
				for (String split : eachSplits) {
					if (split.indexOf("Location:") >= 0) {
						String locationStr = split.substring(split.indexOf("Location:") + "Location:".length());
						locationStr = locationStr.trim();
						Location location = new Location();
						location.setName(locationStr);
						if ("CA".equalsIgnoreCase(location.getName())) {
							location.setSalesTaxRate(new Double(Double.parseDouble(salesTaxRateCA)));
							location.setExempts(exemptCASet);
						}
						else if ("NY".equalsIgnoreCase(location.getName())) {
							location.setSalesTaxRate(new Double(Double.parseDouble(salesTaxRateNY)));
							location.setExempts(exemptNYSet);
						}
						eachPurchase.setLocation(location);
					}
					else {
						Product eachProduct = new Product();
						int atIdxSt = split.indexOf(" at ");
						int atIdxEd = atIdxSt + " at ".length();
						String info1 = split.substring(0, atIdxSt);
						info1 = info1.trim();
						String info2 = split.substring(atIdxEd);
						info2 = info2.trim();
						Double quantity = null;
						StringBuffer quantityStr = new StringBuffer();
						int k = 0;
						for (; k < info1.length(); k ++) {
							if (Character.isDigit(info1.charAt(k))) {
								quantityStr.append(info1.charAt(k)+"");
							}
							else {
								if (quantityStr.toString().length() > 0) {
									break;
								}
							}
						}
						if (quantityStr.toString().length() > 0) {
							quantity = new Double(Double.parseDouble(quantityStr.toString().trim()));
						}
						if (quantity != null) {
							eachProduct.setQuantity(quantity);
						}
						String prodName = info1.substring(k);
						prodName = prodName.trim();
						eachProduct.setName(prodName);
						Double price = new Double(Double.parseDouble(info2));
						eachProduct.setPrice(price);
						Double tax = null;
						if (eachPurchase.getLocation() != null 
								&& eachPurchase.getLocation().getName() != null) {
							if ("CA".equalsIgnoreCase(eachPurchase.getLocation().getName())) {
								if (cateFoodSet.contains(eachProduct.getName())) {
									tax = new Double(0);
								}
								else {
									tax = new Double(Double.parseDouble(salesTaxRateCA));
								}
							}
							else if ("NY".equalsIgnoreCase(eachPurchase.getLocation().getName())) {
								if (cateFoodSet.contains(eachProduct.getName()) || cateClothSet.contains(eachProduct.getName())) {
									tax = new Double(0);
								}
								else {
									tax = new Double(Double.parseDouble(salesTaxRateNY));
								}
							}
						}
						eachProduct.setTax(tax);
						products.add(eachProduct);
					}
				}
				eachPurchase.setProducts(products);
				purchases.add(eachPurchase);
			}
		}
		
		System.out.println("parseInput end");
		
		return purchases;
	}
	
	public Double computeSalesTax(Double price, Integer quantity, Double salesTaxRate) {
		System.out.println("computeSalesTax start");
		Double result;
		Double tmp = new Double(Math.round(price.doubleValue() * quantity.doubleValue() * salesTaxRate.doubleValue()));
		int idx = tmp.toString().indexOf('.');
		idx += 3;
		String tmpStr = null;
		if ((idx+1)<tmp.toString().length()) {
			tmpStr = tmp.toString().substring(0, idx+1).trim();
		} else {
			tmpStr = tmp.toString().substring(0).trim();
		}
		result = new Double(Double.parseDouble(tmpStr));
		System.out.println("computeSalesTax end");
		return result;
	}
	// input should be precious to 3 digits, like 1.13, 1.151
	public Double roundedUp(Double input) {
		System.out.println("roundedUp start");
		System.out.println("input:"+input);
		Double result;
		Double tmp = input.doubleValue() * Math.pow(10, 3);
		String tmpStr = tmp.toString().trim().substring(0, tmp.toString().trim().indexOf('.'));
		int tmpLen = tmpStr.length();
		String tmpStrChecked = tmpStr.substring(tmpLen-2, tmpLen);
		Integer checked;
		if (tmpStrChecked.charAt(0) == '0') {
			checked = new Integer(Integer.parseInt(tmpStrChecked.charAt(1)+""));
		} else {
			checked = new Integer(Integer.parseInt(tmpStrChecked));
		}
		if (checked.intValue() > 0 && checked.intValue() < 10) {
			tmpStrChecked = "50";
		} else if (checked.intValue() >= 10 && checked.intValue() < 50) {
			tmpStrChecked = "50";
		} else if (checked.intValue() > 50 && checked.intValue() < 100) {
			tmpStrChecked = "00";
		}
		
		if ("00".equalsIgnoreCase(tmpStrChecked)) {
			tmpStr = tmpStr.substring(0, tmpStr.length()-2).concat("00");
			Integer addOne = null;
			int ptr = tmpStr.length() - 3;
			for (; ptr >= 0; ptr --) {
				Integer eachDigit = new Integer(Integer.parseInt(tmpStr.charAt(ptr)+""));
				if (eachDigit.intValue() >= 0 && eachDigit.intValue() < 9) {
					addOne = new Integer(eachDigit.intValue() + 1);
					break;
				}
			}
			if (addOne != null) {
				StringBuffer zeros = new StringBuffer();
				for (int i = ptr + 1; i < tmpStr.length(); i ++) {
					zeros.append("0");
				}
				tmpStr = tmpStr.substring(0, ptr) + addOne.toString() + zeros.toString();
			}
		} else {
			tmpStr = tmpStr.substring(0, tmpStr.length()-2).concat("50");
		}
		
		Integer tmpInteger = new Integer(Integer.parseInt(tmpStr));
		tmp = new Double(tmpInteger.doubleValue() / Math.pow(10, 3));
		int dotIdx = tmp.toString().indexOf('.');
		String tmptmp = null;
		if (dotIdx+3 <= tmp.toString().length()) {
			tmptmp = tmp.toString().substring(0, dotIdx+3);
		}
		else {
			tmptmp = tmp.toString().substring(0);
		}
		
		result = new Double(Double.parseDouble(tmptmp));
		System.out.println("result: "+result);
		System.out.println("roundedUp end");
		
		return result;
	}
	public Double preciousTo2Digit(Double input) {
		BigDecimal b = new BigDecimal(input.doubleValue());
		return new Double(b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
	}
	//Double computeSalesTax(Double price, Integer quantity, Double salesTaxRate)
	//Double roundedUp(Double input)
	public LinkedList<PurchaseOutput> computeAll(LinkedList<Purchase> purchases) {
		System.out.println("computeAll start");
		if (purchases == null || purchases.size() < 1) {
			return null;
		}
		
		LinkedList<PurchaseOutput> purchaseOutput = new LinkedList<PurchaseOutput>();
		for (Purchase eachPurchase : purchases) {
			PurchaseOutput aPurchaseOutput = new PurchaseOutput();
			// TODO: compute Sales Tax
			if (eachPurchase.getProducts() != null && eachPurchase.getProducts().size() > 0) {
				aPurchaseOutput.setProducts(eachPurchase.getProducts());
				double subtotal = 0;
				double tax = 0;
				double total = 0;
				for (Product eachProd : eachPurchase.getProducts()) {
					subtotal += eachProd.getPrice() * eachProd.getQuantity();
					tax += eachProd.getPrice() * eachProd.getQuantity() * eachProd.getTax();
				}
				tax = roundedUp(new Double(tax)).doubleValue();
				total = subtotal + tax;
				aPurchaseOutput.setSubtotal(preciousTo2Digit(new Double(subtotal)));
				aPurchaseOutput.setTax(new Double(tax));
				aPurchaseOutput.setTotal(preciousTo2Digit(new Double(total)));
			}
			//
			purchaseOutput.add(aPurchaseOutput);
		}
		System.out.println("computeAll end");
		
		return purchaseOutput;
	}
	
	public void print(LinkedList<PurchaseOutput> output) {
		System.out.println("print start");
		FileOutputStream fop = null;
		File file;
		try {
			file = new File(currentSysPath+"\\src\\resources\\output.txt");
			fop = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			for (PurchaseOutput each : output) {
				String headers = "item\t\t\tprice\t\t\tqty";
				fop.write(headers.getBytes());
				fop.write("\n".getBytes());
				LinkedList<Product> prods = each.getProducts();
				if (prods != null && prods.size() > 0) {
					for (Product eachProd : prods) {
						String tmp = eachProd.getName() + "\t\t\t$" + eachProd.getPrice().toString()+ "\t\t\t" + eachProd.getQuantity().intValue();
						fop.write(tmp.getBytes());
						fop.write("\n".getBytes());
					}
				}
				String taxPrecious2 = each.getTax().toString();
				int idxPre = taxPrecious2.indexOf(".");
				int endPre = taxPrecious2.length()-1;
				if (endPre-idxPre < 2) {
					taxPrecious2 = each.getTax().toString().concat("0");
				}
				String subtotalStr = "subtotal:\t\t\t\t\t\t$"+each.getSubtotal().toString();
				String taxStr = "tax:\t\t\t\t\t\t$"+taxPrecious2;
				String totalStr = "total:\t\t\t\t\t\t$" + each.getTotal().toString();
				fop.write(subtotalStr.getBytes());
				fop.write("\n".getBytes());
				fop.write(taxStr.getBytes());
				fop.write("\n".getBytes());
				fop.write(totalStr.getBytes());
				fop.write("\n".getBytes());
				fop.write("\n\n\n\n\n".getBytes());
			}
			fop.flush();
			fop.close();
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("print end");
	}
	
	public static void main(String[] args) {
		ShoppingReceipt aShoppingReceipt = new ShoppingReceipt();
		aShoppingReceipt.loadProperties();
		LinkedList<String> input = aShoppingReceipt.readInput(currentSysPath+inputFilePath);
		LinkedList<Purchase> purchases = aShoppingReceipt.parseInput(input);
		LinkedList<PurchaseOutput> output = aShoppingReceipt.computeAll(purchases);
		aShoppingReceipt.print(output);
		
		return;
	}
}
