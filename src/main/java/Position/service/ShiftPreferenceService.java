package Position.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Position.entity.ShiftPreferenceDetailEntity;
import Position.entity.ShiftPreferenceEntity;
import Position.repository.ShiftPreferenceDetailRepository;
import Position.repository.ShiftPreferenceRepository;
import Position.Employee;
import Position.ShiftPreference;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class ShiftPreferenceService {

    private final ShiftPreferenceRepository headerRepo;
    private final ShiftPreferenceDetailRepository detailRepo;
    private final EmployeeService employeeService;

    public ShiftPreferenceService(
            ShiftPreferenceRepository headerRepo,
            ShiftPreferenceDetailRepository detailRepo,
            EmployeeService employeeService
    ) {
        this.headerRepo = headerRepo;
        this.detailRepo = detailRepo;
        this.employeeService = employeeService;
    }

    /**
     * 希望の保存：
     *  - header を取得（なければ新規作成）
     *  - details を全削除
     *  - availabilityMap を明細として再保存
     */
    public void savePreference(ShiftPreference pref) {

        int empNum = pref.getEmployee().getId();
        LocalDate date = pref.getDate();

        // ① 既存ヘッダを取得
        ShiftPreferenceEntity existing =
                headerRepo.findByEmployeeNumberAndDate(empNum, date);

     // ② 新規の場合だけ作成 
        if (existing == null) { 
        	existing = new ShiftPreferenceEntity(); 
        	existing.setEmployeeNumber(empNum); 
        	existing.setDate(date); 
        	existing = headerRepo.save(existing); 
        	}
        ShiftPreferenceEntity header = existing;

        // ③ 明細を削除
        detailRepo.deleteAll(detailRepo.findByHeaderId(header.getId()));

        // ④ 明細を再保存
        pref.getAvailabilityMap().forEach((shiftTime, available) -> {
            ShiftPreferenceDetailEntity detail = new ShiftPreferenceDetailEntity();
            detail.setHeader(header);
            detail.setShiftTime(shiftTime.name());
            detail.setAvailability(available);
            detailRepo.save(detail);
        });
    }

    /**
     * 従業員 + 日付 で 1 件取得
     */
    public ShiftPreference findOne(int employeeNumber, LocalDate date) {

        ShiftPreferenceEntity header =
                headerRepo.findByEmployeeNumberAndDate(employeeNumber, date);

        if (header == null) return null;

        List<ShiftPreferenceDetailEntity> details =
                detailRepo.findByHeaderId(header.getId());

        Employee emp = employeeService.findByEmployeeNumber(employeeNumber);

        Map<Position.ShiftTime, Integer> availability = new HashMap<>();

        for (ShiftPreferenceDetailEntity d : details) {
            availability.put(
                    Position.ShiftTime.valueOf(d.getShiftTime()),
                    d.getAvailability()
            );
        }

        return new ShiftPreference(emp, availability, date);
    }

    /**
     * ある社員の全希望
     */
    public List<ShiftPreference> findByEmployeeNumber(int employeeNumber) {
        List<ShiftPreferenceEntity> headers =
                headerRepo.findByEmployeeNumber(employeeNumber);

        Employee emp = employeeService.findByEmployeeNumber(employeeNumber);

        List<ShiftPreference> list = new ArrayList<>();

        for (ShiftPreferenceEntity header : headers) {
            List<ShiftPreferenceDetailEntity> details =
                    detailRepo.findByHeaderId(header.getId());

            Map<Position.ShiftTime, Integer> availability = new HashMap<>();

            for (ShiftPreferenceDetailEntity d : details) {
                availability.put(
                        Position.ShiftTime.valueOf(d.getShiftTime()),
                        d.getAvailability()
                );
            }

            list.add(new ShiftPreference(emp, availability, header.getDate()));
        }

        return list;
    }
    
    //特定の日付の希望シフトを取得
    public List<ShiftPreference> findByDate(LocalDate date){
    	List<ShiftPreferenceEntity> headers =
    			headerRepo.findByDate(date);
    	
    	List<ShiftPreference> list = new ArrayList<>();
    	
    	for(ShiftPreferenceEntity header : headers) {
    		
    		Employee emp = employeeService.findByEmployeeNumber(header.getEmployeeNumber());
    		
    		List<ShiftPreferenceDetailEntity> details = 
    				detailRepo.findByHeaderId(header.getId());
    		
    		Map<Position.ShiftTime, Integer> availability = new HashMap<>();
    		
    		for(ShiftPreferenceDetailEntity d : details) {
    			availability.put(
    					Position.ShiftTime.valueOf(d.getShiftTime()),
    					d.getAvailability()
    			);
    		}
    		
    		//ShiftPreferenceを1件分作ってリストの追加
    		list.add(new ShiftPreference(emp, availability, header.getDate()));
    	}
    	return list;
    }
}
