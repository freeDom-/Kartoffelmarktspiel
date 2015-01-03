package jKMS.states;

import jKMS.Amount;
import jKMS.Kartoffelmarktspiel;
import jKMS.Pdf;
import jKMS.cards.BuyerCard;
import jKMS.cards.Card;
import jKMS.cards.SellerCard;
import jKMS.exceptionHelper.EmptyFileException;
import jKMS.exceptionHelper.FalseLoadFileException;
import jKMS.exceptionHelper.WrongAssistantCountException;
import jKMS.exceptionHelper.WrongFirstIDException;
import jKMS.exceptionHelper.WrongPlayerCountException;
import jKMS.exceptionHelper.WrongRelativeDistributionException;
import jKMS.LogicHelper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;

public class Preparation extends State	{

	private Pdf pdf;

	
	public Preparation(Kartoffelmarktspiel kms){
		this.kms = kms;
		this.pdf = new Pdf();
	}
	
	//	Loads StandardConfiguration into kms.
	//	This method only loads the relative values for displaying in Web Interface.
	//	Absolute Values are calculated by Javascript and stored using the newGroup-Method.
	@Override
	public void loadStandardDistribution(){

		// Load Buyer Distribution
		Map<Integer, Amount> bDistribution = new TreeMap<>();
		bDistribution.put(70, new Amount(20, 0));
		bDistribution.put(65, new Amount(16, 0));
		bDistribution.put(60, new Amount(16, 0));
		bDistribution.put(55, new Amount(16, 0));
		bDistribution.put(50, new Amount(16, 0));
		bDistribution.put(45, new Amount(16, 0));
		kms.getConfiguration().setbDistribution(bDistribution);
		
		// Load Seller Distribution
		Map<Integer, Amount> sDistribution = new TreeMap<>();
		sDistribution.put(63, new Amount(10, 0));
		sDistribution.put(58, new Amount(18, 0));
		sDistribution.put(53, new Amount(18, 0));
		sDistribution.put(48, new Amount(18, 0));
		sDistribution.put(43, new Amount(18, 0));
		sDistribution.put(38, new Amount(18, 0));
		kms.getConfiguration().setsDistribution(sDistribution);
		
		// Set Amount of Groups
		kms.getConfiguration().setGroupCount(6);
		
		System.out.println("Loaded Standard Distribution.");
		
	}
	
	
	//setBasicConfig
	//setter method for the number of players and assistants
	@Override
	public void setBasicConfig(int playerCount, int assistantCount){
		kms.getConfiguration().setPlayerCount(playerCount);
		kms.getConfiguration().setAssistantCount(assistantCount);
		System.out.println("SettingBasicConfiguration:");
		System.out.println("PlayerCount = " + kms.getConfiguration().getPlayerCount());
		System.out.println("AssistantCount = " + kms.getConfiguration().getAssistantCount());
	}
	
	
	//load Implementieren
	@Override
	public void load(MultipartFile file) throws NumberFormatException, IOException, EmptyFileException, FalseLoadFileException{
	//set initial value for load
    	int groupCount=0;
    	int firstID=0;
    	Set<Card> cardSet = new LinkedHashSet<Card>();
    	Map<Integer, Amount> bDistributionLoad = new TreeMap<>();
		Map<Integer, Amount> sDistributionLoad = new TreeMap<>();
		//deal with PlayerCount,AssistantCount,GroupCount and fistID
    	 if (!file.isEmpty()) {
            	 BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
            	 String buf = "";
            	 int count = 0;
            	 while ((buf=br.readLine()) != null && count < 4) {
            		 buf=buf.trim();
            		 String[] sa = buf.split(":|\\s");
            		 //in State Preparation did not load PlayerCount
            		 if(count == 0){
            			 count = count + 1;
            			 continue;
            		 }
            		 //in State Preparation did not load AssistantCount
            		 else if(count == 1){
            			 count = count + 1;
            			 continue;
            		 }
            		 else if(count == 2){
            			 groupCount = Integer.valueOf(sa[1].trim());
            			 count = count + 1;
            			 continue;
            		 }
            		 else if(count == 3){
            			 firstID = Integer.valueOf(sa[1].trim());
            			 if(firstID != 1001){
            				 throw new FalseLoadFileException("firsstID is not 1001,please do not change the load file!");
            			 }
            			 count = count + 1;
            			 break;
            		 }
            	 }
            	 System.out.println("GroupCount:"+groupCount);
    			 System.out.println("firstID:"+firstID);
            	 System.out.println("load GroupCount and fistID successful");
            	 //load bDistribution and sDistribution
            	 while ( count >=4 && count < groupCount+4){
            		 if( (buf=br.readLine()) != null){
	            		 buf=buf.trim();
	 		             String[] sa = buf.split(":|\\s");
	 		             if(sa[0].equals("bDistribution") && sa[4].equals("sDistribution")){
		 		             int bpreis = Integer.valueOf(sa[1].trim());
		 		             Amount bAmount =  new Amount(Integer.valueOf(sa[2].trim()),Integer.valueOf(sa[3].trim()));
		 		             // int banteil = Integer.valueOf(sa[1]);
		 		             int spreis = Integer.valueOf(sa[5].trim());
		 		             Amount sAmount = new Amount(Integer.valueOf(sa[6].trim()),Integer.valueOf(sa[7].trim()));
		 		             //int santeil = Integer.valueOf(sa[3]);
		 		            
		 		             bDistributionLoad.put(bpreis, bAmount);
		 		             sDistributionLoad.put(spreis, sAmount);
		 		             count = count + 1;
	 		             }else{
	 		            	 throw new FalseLoadFileException("the nember of sDistribution should be equal to the number of bDistribution!");
	 		             }
            		 }else {
            			 throw new FalseLoadFileException("The GroupCount is not right,please do not change the load file!");
            		 }
            	 }
            	 buf = br.readLine();
            	 buf = buf.trim();
            	 String[] sa = buf.split(":|\\s");
            	 if(sa[0].equals("bDistribution") && sa[4].equals("sDistribution")){
            		 throw new FalseLoadFileException("the nember of bDistribution should be equal to groupCount,the nember of sDistribution should be equal to groupCount");
            	 }
            	 System.out.println("bDistribution:"+bDistributionLoad.toString());
    			 System.out.println("sDistribution:"+sDistributionLoad.toString());
            	 System.out.println("load bDistribution and sDistribution successful");
            	 // TODO discuss wether loading Cards
            	 //load Cards and set them in cardSet
//            	 while (count >= groupCount +4 && (buf=br.readLine()) != null){
//            		 Card card;
//            		 buf=buf.trim();
//            		 String[] sa = buf.split(":|\\s");
//            		 if((Integer.valueOf(sa[1])%2) == 1){
//            			card = new BuyerCard(Integer.valueOf(sa[1].trim()),Integer.valueOf(sa[2].trim()),sa[3].trim().charAt(0));
//            		 }else {
//            			card = new SellerCard(Integer.valueOf(sa[1].trim()),Integer.valueOf(sa[2].trim()),sa[3].trim().charAt(0));
//            		 }
//            		 cardSet.add(card);
//            	 }
//            	 System.out.println("Cards Number:"+cardSet.size());
//            	 System.out.println("load cardSet successful");

    			 //set load information in Configuration
    	    	 kms.getConfiguration().setGroupCount(groupCount);
    	    	 kms.getConfiguration().setFirstID(firstID);
    	    	 kms.getConfiguration().setbDistribution(bDistributionLoad);
    			 kms.getConfiguration().setsDistribution(sDistributionLoad);
//    			 kms.setCards(cardSet);
    			 
         }else 
             throw new EmptyFileException("load file can not be empty, please do not delete loadfile!");
    	 
    	 System.out.println("load() successful!");
    	 
    }

	
		
		//defalt path:Users/yangxinyu/git/jKMS
	@Override
	public boolean save(OutputStream o) throws IOException{
		//take out information aus Configuration and kms
		 Map<Integer, Amount> bDistributionSave = new TreeMap<>();
		 Map<Integer, Amount> sDistributionSave = new TreeMap<>();
		 bDistributionSave = kms.getConfiguration().getbDistribution();
		 sDistributionSave = kms.getConfiguration().getsDistribution();
		 //create outputformat for the outputstream 
		 if(bDistributionSave.isEmpty() || sDistributionSave.isEmpty())
			 return false;
		 else{
			 
				   String line = System.getProperty("line.separator");
				   StringBuffer str = new StringBuffer();
				// FileWriter fw = new FileWriter(path, false);
				   str.append("PlayerCount:").append(kms.getConfiguration().getPlayerCount()).append(line)
				   .append("AssistantCount:").append(kms.getConfiguration().getAssistantCount()).append(line)
				   .append("GroupCount:").append(kms.getConfiguration().getGroupCount()).append(line)
				   .append("FirstID:").append(kms.getConfiguration().getFirstID()).append(line);
				   
				   Set bSet = bDistributionSave.entrySet();
				   Set sSet = sDistributionSave.entrySet();
				   Iterator bIter = bSet.iterator();
				   Iterator sIter = sSet.iterator();
				   while(bIter.hasNext() && sIter.hasNext()){
					   Map.Entry bEntry = (Map.Entry)bIter.next(); 
					   Map.Entry sEntry = (Map.Entry)sIter.next(); 
				    
					   str.append("bDistribution:"+bEntry.getKey()+":"+((Amount) bEntry.getValue()).getRelative()+":"+((Amount) bEntry.getValue()).getAbsolute()+
							   " "+"sDistribution:"+sEntry.getKey()+":"+((Amount)sEntry.getValue()).getRelative()+":"+((Amount)sEntry.getValue()).getAbsolute()).append(line);
				   }
				   Set cardSet = kms.getCards();
				   Iterator cardIter = cardSet.iterator();
				   while(cardIter.hasNext()){
					   Card card = (Card) cardIter.next();
					   str.append("Card:"+card.getId()+":"+card.getValue()+":"+card.getPackage()).append(line);
				   }
				   System.out.println("create outputstreamformat successful");
				   //write information to file
				   if(o instanceof FileOutputStream){
					   FileOutputStream fo = (FileOutputStream)o;
					   fo.write(str.toString().getBytes());
					   fo.close();
				   }
				   else if(o instanceof ByteArrayOutputStream){
					   ByteArrayOutputStream bo = (ByteArrayOutputStream)o;
					   bo.write(str.toString().getBytes());
					   bo.close();
				   }
				   else{
					   return false;
				   }
				 //fw.write(str.toString());
				 //fw.close();
				   System.out.println("save() successful");
			       return true;
		 }
	}



	
	// generateCardSet
	// Generate an ordered, random Set of Cards using
	// bDistribution and sDistribution
	@Override
	public void generateCards() throws WrongRelativeDistributionException, WrongAssistantCountException, WrongFirstIDException, WrongPlayerCountException {
		// DECLARATION
		
		//for put seller and buyer distribution
		Random random = new Random();
		
		List<Integer> bKeys = new ArrayList<Integer>(kms.getConfiguration().getbDistribution().keySet());
		List<Integer> sKeys = new ArrayList<Integer>(kms.getConfiguration().getsDistribution().keySet());
		Map<Integer, Amount> bTemp = new TreeMap<Integer, Amount>(kms.getConfiguration().getbDistribution());
		Map<Integer, Amount> sTemp = new TreeMap<Integer, Amount>(kms.getConfiguration().getsDistribution());
		
		int randomKey, randomListEntry;
		
		//for put packages
		int packid,ide, id;
		int[] packdistribution =LogicHelper.getPackageDistribution(kms.getPlayerCount(),kms.getAssistantCount());
		
		//clear Card Set
		kms.getCards().clear();

		// IMPLEMENTATION
		
		//test is there a conform configuration?
		if(kms.getPlayerCount() != (LogicHelper.getAbsoluteSum(bTemp) +  LogicHelper.getAbsoluteSum(sTemp)))throw new WrongPlayerCountException();
		if(kms.getAssistantCount() <= 0)throw new WrongAssistantCountException();
		if(kms.getConfiguration().getFirstID() < 0)throw new WrongFirstIDException();
		if((LogicHelper.getRelativeSum(bTemp) +  LogicHelper.getRelativeSum(sTemp)) != 200) throw new WrongRelativeDistributionException(); // muss in der summe 200 ergeben, da jede distribution in sich 100 ergibt
		
		
		
		

		packid =0;//package index 0 for Pack A, 1 for Pack B ...		
		ide = kms.getConfiguration().getFirstID() + packdistribution[0];
		id  = kms.getConfiguration().getFirstID();
		
		//put seller and buyer distribution and put packages		
		while ((bTemp.isEmpty() != true) || (sTemp.isEmpty() != true)) {
			
			// Create Buyer Card
			if((id % 2) == 1){ // TODO TEST !!!
				//all buyercards have an uneven id
				randomListEntry = random.nextInt(bKeys.size());
				randomKey = bKeys.get(randomListEntry);
			
				if(id < ide){
					kms.getCards().add(new BuyerCard(id, randomKey, LogicHelper.IntToPackage(packid)));
				}else {
					if(id < kms.getConfiguration().getFirstID() + kms.getPlayerCount()){
						packid++;
						ide = ide + packdistribution[packid];
						kms.getCards().add(new BuyerCard(id, randomKey, LogicHelper.IntToPackage(packid)));
					}
				}


		
				bTemp.put(randomKey, new Amount(bTemp.get(randomKey).getRelative(), bTemp.get(randomKey).getAbsolute() - 1)); 
				if (bTemp.get(randomKey).getAbsolute() == 0) {
					bTemp.remove(randomKey);
					bKeys.remove(randomListEntry);
				}

			 id++;
			 
			}else{

				// Create Seller Card
				randomListEntry = random.nextInt(sKeys.size());
				randomKey = sKeys.get(randomListEntry);

				if(id < ide){
					kms.getCards().add(new SellerCard(id, randomKey, LogicHelper.IntToPackage(packid)));
				}else {
				//a new package start 
					if(id < kms.getConfiguration().getFirstID() + kms.getPlayerCount()){
						//it's in 
						packid++;
						ide = ide + packdistribution[packid];
						kms.getCards().add(new SellerCard(id, randomKey, LogicHelper.IntToPackage(packid)));
					}
				}

				sTemp.put(randomKey, new Amount(sTemp.get(randomKey).getRelative(), sTemp.get(randomKey).getAbsolute() - 1));
				if (sTemp.get(randomKey).getAbsolute() == 0) {
					sTemp.remove(randomKey);
					sKeys.remove(randomListEntry);
				}

			 id++;
		  }
		}
		
	}			

	// newGroup
	// Creates a new entry for the bDistribution or sDistribution Map,
	// depending if isBuyer is true or false.
	@Override
	public void newGroup(boolean isBuyer, int price, int relativeNumber, int absoluteNumber) {
		Map<Integer, Amount> distrib;
		
		if (isBuyer) distrib = kms.getConfiguration().getbDistribution();
		else distrib = kms.getConfiguration().getsDistribution();
		
		if(!distrib.containsKey(price))
			distrib.put(price,  new Amount(relativeNumber, absoluteNumber));
		else	{ 
			distrib.get(price).setAbsolute(distrib.get(price).getAbsolute() + absoluteNumber);
			distrib.get(price).setRelative(distrib.get(price).getRelative() + relativeNumber);
		}
		
		System.out.println("Registered the following Group:");
		Amount registered = distrib.get(price);
		if(isBuyer)	{
			System.out.println("Buyer: " + price + "€ " + registered.getRelative() + "% " + registered.getAbsolute());
		}	else	{
			System.out.println("Seller: " + price + "€ " + registered.getRelative() + "% " + registered.getAbsolute());
		}
		
	}
	
	// createPDF - Delegates to PDF-Class
	@Override
	public void createPdf(boolean isBuyer, Document doc) throws DocumentException,IOException	{
		
		if(isBuyer)	{
			pdf.createPdfCardsBuyer(doc, kms.getCards(), kms.getAssistantCount(), kms.getConfiguration().getFirstID());
		}	else	{
			pdf.createPdfCardsSeller(doc, kms.getCards(), kms.getAssistantCount(), kms.getConfiguration().getFirstID());
		}
		
		System.out.println("PDF Created!");
	}
}
