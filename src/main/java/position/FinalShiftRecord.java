package position;

import java.time.LocalDate;

public class FinalShiftRecord {
	private final Employee employee;
	
	private final ShiftTime shiftTime;
	
	private final LocalDate date;

	private final Pos position;
	public FinalShiftRecord(Employee employee, ShiftTime shiftTime, LocalDate localDate, Pos pos) {
		this.employee = employee;
		this.shiftTime = shiftTime;
		this.date = localDate;
		this.position = pos;
	}
	
	public Employee getEmployee() {
		return employee;
	}
	
	public ShiftTime getShiftTime() {
		return shiftTime;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public Pos getPos() {
		return position;
	}
}