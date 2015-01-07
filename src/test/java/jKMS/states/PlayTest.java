package jKMS.states;

import java.util.TreeMap;

import jKMS.Amount;
import jKMS.Application;
import jKMS.Kartoffelmarktspiel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class PlayTest {
	@Autowired
	private Kartoffelmarktspiel kms;
	
	@Before
	public void setUp(){
		kms.prepare();
		kms.getCards().clear();
		kms.getConfiguration().setbDistribution(new TreeMap<Integer, Amount>());
		kms.getConfiguration().setsDistribution(new TreeMap<Integer, Amount>());
		
		//Setup Player/Assistant No 
		kms.getState().setBasicConfig(10, 1);
		kms.getConfiguration().setFirstID(1001);
		
		//Setup Distribution pro distribution 100 % because eingabemaske
		kms.getState().newGroup(true, 2, 40, 2);
		kms.getState().newGroup(true, 3, 40, 2);
		kms.getState().newGroup(true, 4, 20, 1);
		kms.getState().newGroup(false, 2, 20, 1);
		kms.getState().newGroup(false, 3, 40, 2);
		kms.getState().newGroup(false, 4, 40, 2);
		
		try{
			kms.getState().generateCards();
		}catch (Exception e) {
			e.printStackTrace();	
		}
		
		
		kms.load();
		kms.play();
	}
	
	@Test
	public void testAddContract(){
				//BuyerCard(1001,2,'A')
				//BuyerCard(1003,2,'A')
				//BuyerCard(1005,3,'A')
				//BuyerCard(1007,3,'A')
				//BuyerCard(1009,4,'A')
				//SellerCard(1002,2,'A')
				//SellerCard(1004,3,'A')
				//SellerCard(1006,3,'A')
				//SellerCard(1008,4,'A')
				//SellerCard(1010,4,'A')
		
			//set the state to play
				kms.play();
			//test addContract()	
				assertEquals("addContract() should return 0 when input both right cardId,id1 and id2 can exchange"
						,0, kms.getState().addContract(1001, 1010, 6,"TEST"));
				assertEquals("addContract() should return 0 when input both right cardId,id1 and id2 can exchange"
						,0, kms.getState().addContract(1002, 1009, 6,"TEST"));
				assertEquals("addContract() should return 1 when input double Buyer "
						,1, kms.getState().addContract(1003, 1005, 6,"TEST"));
				assertEquals("addContract() should return 1 when input double Seller "
						,1, kms.getState().addContract(1004, 1006, 6,"TEST"));
				// TODO discuss
				assertEquals("addContract() should return 2 when input 2 same Id number,also a Id is invalid "
						,1, kms.getState().addContract(1006, 1006, 6,"TEST"));
				assertEquals("addContract() should return 2 when a cardId is not available "
						,2, kms.getState().addContract(1011, 1006, 6,"TEST"));
				assertEquals("addContract() should return 2 when a cardId is not available "
						,2, kms.getState().addContract(1004, 1021, 6,"TEST"));
				assertEquals("addContract() should return 2 when both cardId are not available "
						,2, kms.getState().addContract(1026, 1031, 6,"TEST"));
				// 1001,1002,1009,1010 are already dealt
				assertEquals("addContract() should return 3 when the Buyer is already dealt "
						,3, kms.getState().addContract(1001, 1004, 6,"TEST"));
				assertEquals("addContract() should return 3 when the Seller is already dealt "
						,3, kms.getState().addContract(1003, 1010, 6,"TEST"));
				assertEquals("addContract() should return 3 when both Buyer and Seller are already dealt "
						,3, kms.getState().addContract(1001, 1002, 6,"TEST"));

	}
	

}
