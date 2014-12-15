package jKMS.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jKMS.Amount;
import jKMS.Contract;
import jKMS.LogicHelper;
import jKMS.exceptionHelper.NoContractsException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.bytecode.opencsv.CSVWriter;

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
		TreeMap<Integer, Amount> sDistribution = (TreeMap<Integer, Amount>) kms.getsDistribution();
		String expectedSupply = ControllerHelper.mapToString(sDistribution);
		
		TreeMap<Integer, Amount> bDistribution = (TreeMap<Integer, Amount>) kms.getbDistribution();
		String expectedDemand = ControllerHelper.mapToString(bDistribution.descendingMap());
		
		
		//min and max values for the chart
		int[] minMax = ControllerHelper.getMinMax(sDistribution, bDistribution);
		
		//concatenate return string
		String str = playData.concat(";" + expectedSupply + ";" + expectedDemand + ";" + minMax[0] + ";" + minMax[1]);
		
		return str;
		
	}
	
	
	/*
	 * gets all attributes of "winner contract" and adds them to the model
	 */
	@RequestMapping(value = "/lottery")
	public String lottery(Model model) throws NoContractsException{
		
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
	public String evaluate(Model model) throws NoContractsException	{
		boolean stateChangeSuccessful = true;
		
		try	{
			stateChangeSuccessful = ControllerHelper.stateHelper(kms, "evaluate");
		}	catch(Exception e)	{
			e.printStackTrace();
			return "error?e=" + e.toString();
		}
		
		if(stateChangeSuccessful)	{
			
			String path = ControllerHelper.getApplicationFolder() + ControllerHelper.getExportFolderName() + "/" + LogicHelper.getLocalizedMessage("filename.csv") + ControllerHelper.getNiceDate() + ".csv";
	    	
			try {
		    	CSVWriter writer = new CSVWriter(new FileWriter(path));
				kms.getState().generateCSV(writer);
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Map<String,Float> stats = kms.getState().getStatistics();
			
			model.addAttribute("average", stats.get("averagePrice"));
			model.addAttribute("min", stats.get("minimum"));
			model.addAttribute("max", stats.get("maximum"));
			model.addAttribute("variance", stats.get("variance"));
			model.addAttribute("standardDeviation", stats.get("standardDeviation"));
			model.addAttribute("eqPrice", stats.get("eqPrice"));
			model.addAttribute("eqQuantity", stats.get("eqQuantity"));
			
			
			return "evaluate";
		}	else	{
			return "reset";
		}
	}
}
