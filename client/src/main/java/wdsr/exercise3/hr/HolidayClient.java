package wdsr.exercise3.hr;

import java.net.URL;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;

import wdsr.exercise3.hr.ws.EmployeeType;
import wdsr.exercise3.hr.ws.HolidayRequest;
import wdsr.exercise3.hr.ws.HolidayType;
import wdsr.exercise3.hr.ws.HumanResource;
import wdsr.exercise3.hr.ws.HumanResourceService;

// TODO Complete this class to book holidays by issuing a request to Human Resource web service.
// In order to see definition of the Human Resource web service:
// 1. Run HolidayServerApp.
// 2. Go to http://localhost:8090/holidayService/?wsdl
public class HolidayClient {
	private HumanResourceService service;
	private HumanResource resource;
	
	/**
	 * Creates this object
	 * @param wsdlLocation URL of the Human Resource web service WSDL
	 */
	public HolidayClient(URL wsdlLocation) {
		service = new HumanResourceService(wsdlLocation);	
		resource = service.getHumanResourcePort();	
	}
	
	/**
	 * Sends a holiday request to the HumanResourceService.
	 * @param employeeId Employee ID
	 * @param firstName First name of employee
	 * @param lastName Last name of employee
	 * @param startDate First day of the requested holiday
	 * @param endDate Last day of the requested holiday
	 * @return Identifier of the request, if accepted.
	 * @throws ProcessingException if request processing fails.
	 */
	public int bookHoliday(int employeeId, String firstName, String lastName, Date startDate, Date endDate) throws ProcessingException {	
		HolidayRequest request;
		try {
			request = getRequest(creatEmployee(employeeId, firstName, lastName), 
					createHoliday(startDate, endDate));
			return resource.holiday(request).getRequestId();
		} catch (Exception e) {
			throw new ProcessingException();
		}

	}

	private EmployeeType creatEmployee(int employeeId, String firstName, String lastName) {
		EmployeeType employee = new EmployeeType();
		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setNumber(employeeId);
		return employee;
	}
	
	private HolidayType createHoliday(Date startDate, Date endDate) throws DatatypeConfigurationException {
		HolidayType holiday = new HolidayType();
		holiday.setEndDate(endDate);
		holiday.setStartDate(startDate);
		return holiday;
	}
	
	private HolidayRequest getRequest(EmployeeType employee, HolidayType holiday) throws Exception {
		HolidayRequest request = new HolidayRequest();
		request.setEmployee(employee);
		request.setHoliday(holiday);
		return request;
	}
	

}
