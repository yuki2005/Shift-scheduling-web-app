package position;

public enum DayOfWeek {
	MON,
	TUE,
    WED,
    THR,
    FRI,
    SAT,
    SUN;
	
	public boolean isWeekend() {
		return (this == SAT || this == SUN);
	}	
}
     