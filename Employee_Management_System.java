import java.sql.*;
import java.util.Scanner;

public class Employee_Management_System {
	
	static String url="";
	static String user="root";
	static String password="";
	
	static Connection con;
	
	static Scanner a=new Scanner(System.in);
	private static Time time;

	public static void main(String[] args) {
		try {
			con=DriverManager.getConnection(url, user, password);
			con.setAutoCommit(false);
			while(true) {
				System.out.println("");
				System.out.println("EMPLOYEE ATTENDENCE SYSTEM");
				System.out.println("1.Add Employee");
				System.out.println("2.Mark Attendence");
				System.out.println("3.Apply for Leave");
				System.out.println("4.View your reports");
				System.out.println("5.Remove Employee");
				System.out.println("6.Exit");
				System.out.println("");
				System.out.print("Enter Your Option: ");
				int req=a.nextInt();
				
				if(req==1) {
					addemp();
				}
				
				if(req==2) {
					markatt();
				}
				
				if(req==3) {
					applyleave();
				}
				
				if(req==4) {
					viewrecords();
				}
				if(req==5) {
					removeemmp();
				}
				if(req==6) {
					break;
				}
				
			}
		}
		catch(Exception e) {
			System.out.println("Something Went Wrong.................");
			System.out.println(e);
		}
		finally {
			System.out.println("THANK YOU");
		}

	}

	private static void removeemmp() throws SQLException {
	    String queryDeleteAttendance = "DELETE FROM attendance WHERE employee_id=?";
	    PreparedStatement psmtAttendance = con.prepareStatement(queryDeleteAttendance);
	    String queryDeleteEmployee = "DELETE FROM employees WHERE employee_id=?";
	    PreparedStatement psmtEmployee = con.prepareStatement(queryDeleteEmployee);
	    
	    System.out.print("Enter The ID of Employee to Delete: ");
	    int id = a.nextInt();
	    
	    a.nextLine();  // Consume the leftover newline character after nextInt()
	    
	    System.out.println("Are you sure you want to delete this employee?");
	    System.out.println("Press Y to delete, press X to cancel.");
	    String sel = a.nextLine();
	    
	    if (sel.charAt(0) == 'Y') {
	        try {
	            // Delete related attendance records first
	            psmtAttendance.setInt(1, id);
	            int attendanceRows = psmtAttendance.executeUpdate();
	            
	            if (attendanceRows > 0) {
	                System.out.println("Related attendance records deleted.");
	            } else {
	                System.out.println("No related attendance records found.");
	            }
	            
	            // Now delete the employee
	            psmtEmployee.setInt(1, id);
	            int row = psmtEmployee.executeUpdate();
	            
	            if (row > 0) {
	                System.out.println("Employee data deleted.");
	                con.commit();
	            } else {
	                System.out.println("Something went wrong. The employee data is not deleted.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();  // This will show the exact error message and help us debug
	            System.out.println("Something went wrong");
	            con.rollback();
	        }
	    } else {
	        System.out.println("Employee deletion canceled.");
	    }
	}

	private static void viewrecords() throws SQLException {
		a.nextLine();
		String query="SELECT \r\n"
				+ "    employees.employee_id,\r\n"
				+ "    employees.first_name AS employee_name,   -- Assuming you want 'first_name' as employee_name\r\n"
				+ "    attendance.attendance_date AS date,       -- Changed 'date' to 'attendance_date'\r\n"
				+ "    attendance.check_in_time AS in_time,      -- Changed 'in_time' to 'check_in_time'\r\n"
				+ "    attendance.check_out_time AS out_time,    -- Changed 'out_time' to 'check_out_time'\r\n"
				+ "    attendance.status,\r\n"
				+ "    leave_requests.leave_type,\r\n"
				+ "    leave_requests.start_date  -- Changed leave_date to start_date\r\n"
				+ "FROM employees\r\n"
				+ "LEFT JOIN attendance ON employees.employee_id = attendance.employee_id\r\n"
				+ "LEFT JOIN leave_requests ON employees.employee_id = leave_requests.employee_id \r\n"
				+ "    AND attendance.attendance_date = leave_requests.start_date;";
		
		Statement stmt=con.createStatement();
		
		ResultSet rs=stmt.executeQuery(query);
		
		
		while(rs.next()) {
			
			    int empId = rs.getInt("employee_id");
			    String empName = rs.getString("employee_name");
			    Date date = rs.getDate("date");
			    Time inTime = rs.getTime("in_time");
			    Time outTime = rs.getTime("out_time");
			    String status = rs.getString("status");
			    String leaveType = rs.getString("leave_type"); // can be null

			    System.out.println("Employee ID   : " + empId);
		        System.out.println("Employee Name : " + empName);
		        System.out.println("Date          : " + (date != null ? date : "N/A"));
		        System.out.println("In Time       : " + (inTime != null ? inTime : "N/A"));
		        System.out.println("Out Time      : " + (outTime != null ? outTime : "N/A"));
		        System.out.println("Status        : " + (status != null ? status : "N/A"));

		        if ("Leave".equalsIgnoreCase(status)) {
		            System.out.println("Leave Type    : " + (leaveType != null ? leaveType : "N/A"));
		        }

			    System.out.println("-------------------------------");
			}
		}
		
	

	private static void applyleave() throws SQLException {
		a.nextLine();
		String qurery="insert into leave_requests(employee_id,leave_type,start_date,end_date,status) values(?,?,?,?,?)";
		
		PreparedStatement psmt=con.prepareStatement(qurery);
		System.out.print("Enter your id: ");
		int id=a.nextInt();
		a.nextLine();
		System.out.println("-Sick Leave\r\n"
				+ "\r\n"
				+ "-Casual Leave\r\n"
				+ "\r\n"
				+ "-Medical Leave\r\n"
				+ "\r\n"
				+ "-Emergency Leave\r\n"
				+ "\r\n"
				+ "-Personal Leave\r\n"
				+ "\r\n"
				+ "-Academic Leave (for events, internships, etc.)\r\n"
				+ "\r\n"
				+ "-Leave on Duty (OD)"
				+ "-Others ");
		System.out.print("Enter Leave Type: ");
		String leavtyp=a.nextLine();
		System.out.print("Enter Start_Date(YYYY-MM-DD): ");
		String sdate=a.nextLine();
		System.out.print("Enter End_Date(YYYY-MM-DD): ");
		String edate=a.nextLine();
		System.out.print("Status(Present or Absent): ");
		String outim=a.nextLine();
		
		psmt.setInt(1, id);
		psmt.setString(2, leavtyp);
		psmt.setString(3, sdate);
		psmt.setString(4, edate);
		psmt.setString(5, outim);
		
		
		try {
		int row=psmt.executeUpdate();
		if(row>0) {
			System.out.println("LEAVE REGISTERED SUCESSFULLY");
			con.commit();
		}
		else {
			System.out.println("LEAVE NOT REGISTERED, CONTACT YOUR HEAD");
			con.rollback();
		}
	}
	catch(SQLException e) {
		System.out.println("SOMETHING WENT WRONG");
		System.out.println(e);
	}

			
}
		
	

	private static void markatt() throws SQLException {
		a.nextLine();
		String qurery="insert into attendance(employee_id,attendance_date,status,check_in_time,check_out_time) values(?,?,?,?,?)";
		PreparedStatement psmt=con.prepareStatement(qurery);
		
		System.out.print("Enter your id: ");
		int id=a.nextInt();
		a.nextLine();
		System.out.print("Enter date of Attendence (YYYY-MM-DD): ");
		String date=a.nextLine();
		System.out.print("Enter Status(Present or Absent): ");
		String sta=a.nextLine();
		System.out.print("Enter check-in time(HH:MM:SS): ");
		String intim=a.nextLine();
		System.out.print("Enter check-out time(HH:MM:SS): ");
		String outim=a.nextLine();
		
		psmt.setInt(1, id);
		psmt.setString(2, date);
		psmt.setString(3, sta);
		psmt.setString(4, intim);
		psmt.setString(5, outim);
		
		
		try {
			int row=psmt.executeUpdate();
			
			if(row>0) {
				System.out.println("ATTENDENCE MARED SUCESSFULLY");
				con.commit();
			}
			else {
				System.out.println("ATTENDENCE NOT MARKED, CONTACT YOUR HEAD");
				con.rollback();
			}
		}
		catch(SQLException e) {
			System.out.println("SOMETHING WENT WRONG");
			System.out.println(e);
		}

				
	}

	private static void addemp() throws SQLException {
		a.nextLine();
		String query="insert into employees(first_name,last_name,email,phone,department,job_title,hire_date) values(?,?,?,?,?,?,?)";
		PreparedStatement psmt=con.prepareStatement(query);
		
		
		System.out.print("Enter the First_name: ");
		String fname=a.nextLine();
		

		System.out.print("Enter the Last_name: ");
		String lname=a.nextLine();

		System.out.print("Enter the Mail_id: ");
		String mail=a.nextLine();
		

		System.out.print("Enter the Phone_number: ");
		String Phno=a.nextLine();
		

		System.out.print("Enter the Department: ");
		String dept=a.nextLine();
		

		System.out.print("Enter the Job_title: ");
		String job=a.nextLine();
		

		System.out.print("Enter the hire_date(YYYY-MM-DD): ");
		String hdate=a.nextLine();
		

		psmt.setString(1, fname);
		psmt.setString(2,lname);
		psmt.setString(3,mail);
		psmt.setString(4,Phno);
		psmt.setString(5,dept);
		psmt.setString(6,job);
		psmt.setString(7,hdate);
		
		
		try {
			int row=psmt.executeUpdate();
			if(row>0) {
				System.out.println("DATA ADDED SUCESSFULLY");
				con.commit();
			}
			else {
				con.rollback();
			}
		}
		catch(SQLException e) {
			System.out.println(e);
			System.out.print("Something Went Wrong");
		}
		
	}
	
	
	

}
