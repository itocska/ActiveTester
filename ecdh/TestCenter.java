package ecdh;

import org.openqa.selenium.TimeoutException;

public class TestCenter {
	public static void main(String[] args) throws Throwable {
		try {
		  LoginUser.main(args);
		  TestBase.deleteUserCars();
		  String[] account = {TestBase.personalUser, TestBase.personalPassword};
		  DeleteUser.main(account);
		}
        catch (TimeoutException e) {
			
		}
		//RegisterActivateDeleteCompany.main(args);
		//RegisterWithoutAcceptRules.main(args);
		//RegisterActivateDeleteUser.main(args);
		//LoginUser.main(args);
		//ForgottenPassword.main(args);
		//AddNewCar.main(args);
		//AddNewCarEventFuel.main(args);
		//AddNewCarEventTechspec.main(args);
		SetCarForSale.main(args);
		AddNewCarEventTires.main(args);
		AddNewCarEventCleaning.main(args);
		AddNewCarEventAccident.main(args);
		AddNewCarEventOther.main(args);
		AddNewCarNotes.main(args);
		SendRequestForOfferTire.main(args);
	}
}
