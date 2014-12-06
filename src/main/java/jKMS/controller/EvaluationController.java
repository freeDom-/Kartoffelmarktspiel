package jKMS.controller;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jKMS.Amount;
import jKMS.Contract;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EvaluationController extends AbstractServerController {
	
	@RequestMapping("/getEvaluation")
	@ResponseBody
	/*
	 * catches AjaxRequest, concatenates data list with standardDistribution
	 */
	public String evaluationChart(){
		//String of current play data
		Set<Contract> contracts = kms.getContracts();
		String playData = ControllerHelper.setToString(contracts);
		
		//String of expected supply and demand
		Map<Integer, Amount> sDistribution = kms.getsDistribution();
		String expectedSupply = ControllerHelper.mapToString(sDistribution);
		
		TreeMap<Integer, Amount> bDistribution = (TreeMap<Integer, Amount>) kms.getbDistribution();
		String expectedDemand = ControllerHelper.mapToString(bDistribution.descendingMap());
		
		String str = playData.concat(";" + expectedSupply + ";" + expectedDemand);
		
		return str;
		
	}
	
	
	/*
	 * gets all attributes of "winner contract" and adds them to the model
	 */
	@RequestMapping(value = "/lottery")
	public String lottery(Model model){
		
		boolean stateChangeSuccessful = true;
		
		try	{
			stateChangeSuccessful = ControllerHelper.stateHelper(kms, "evaluate");
		}	catch(Exception e)	{
			e.printStackTrace();
			return "error?e=" + e.toString();
		}
		
		if(stateChangeSuccessful)	{
		
			Contract winner = kms.getState().pickWinner();
			int bProfit = kms.getState().buyerProfit(winner);
			int sProfit = kms.getState().sellerProfit(winner);
			
			model.addAttribute("buyerID", winner.getBuyer().getId());
			model.addAttribute("wtp", winner.getBuyer().getValue());
			model.addAttribute("sellerID", winner.getSeller().getId());
			model.addAttribute("cost", winner.getSeller().getValue());
			model.addAttribute("price", winner.getPrice());
			model.addAttribute("bprofit", bProfit);
			model.addAttribute("sprofit", sProfit);
			
			return "lottery";
		}	else	{
			return "reset";
		}
		
	}
	
	@RequestMapping(value = "/evaluate")
	public String evaluate()	{
		boolean stateChangeSuccessful = true;
		
		try	{
			stateChangeSuccessful = ControllerHelper.stateHelper(kms, "evaluate");
		}	catch(Exception e)	{
			e.printStackTrace();
			return "error?e=" + e.toString();
		}
		
		if(stateChangeSuccessful)	{
			return "evaluate";
		}	else	{
			return "reset";
		}
	}
}
