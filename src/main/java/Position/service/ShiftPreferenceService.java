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

/**
 * 従業員のシフト希望情報を管理するサービスクラス。
 * Controller と Repository の間に位置し、
 * シフト希望の保存・取得に関する業務ロジックを集約する。
 *
 * ・希望シフトは「ヘッダ（従業員＋日付）」と
 *   「時間帯ごとの明細」に分けて管理する
 * ・更新時は既存明細を削除し、最新の希望内容で再作成する
 */

@Service
@Transactional
public class ShiftPreferenceService {

    private final ShiftPreferenceRepository headerRepo;
    private final ShiftPreferenceDetailRepository detailRepo;
    private final EmployeeService employeeService;
    
 // ShiftPreference に関する Repository および EmployeeService を注入するコンストラクタ
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
     * 従業員のシフト希望を保存する。
     *
     * ・従業員番号と日付をキーに希望ヘッダを取得
     * ・存在しない場合は新規作成
     * ・既存の希望明細は全て削除し、最新の希望内容で再保存する
     *
     * @param pref 保存対象のシフト希望情報
     */
    public void savePreference(ShiftPreference pref) {

        int empNum = pref.getEmployee().getId();
        LocalDate date = pref.getDate();

        // 既存ヘッダを取得
        ShiftPreferenceEntity existing =
                headerRepo.findByEmployeeNumberAndDate(empNum, date);

     // 新規の場合だけ作成 
        if (existing == null) { 
        	existing = new ShiftPreferenceEntity(); 
        	existing.setEmployeeNumber(empNum); 
        	existing.setDate(date); 
        	existing = headerRepo.save(existing); 
        	}
        ShiftPreferenceEntity header = existing;

        // 明細を削除
        detailRepo.deleteAll(detailRepo.findByHeaderId(header.getId()));

        // 明細を再保存
        pref.getAvailabilityMap().forEach((shiftTime, available) -> {
            ShiftPreferenceDetailEntity detail = new ShiftPreferenceDetailEntity();
            detail.setHeader(header);
            detail.setShiftTime(shiftTime.name());
            detail.setAvailability(available);
            detailRepo.save(detail);
        });
    }

    /**
     * 従業員番号と日付を指定して、1件のシフト希望を取得する。
     *
     * @param employeeNumber 従業員番号
     * @param date           対象日付
     * @return 該当するシフト希望。存在しない場合は null
     */
    // 将来性を考えての実装
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
     * 指定した従業員の全てのシフト希望を取得する。
     *
     * @param employeeNumber 従業員番号
     * @return シフト希望の一覧
     */
    // 将来性を考えての実装
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
    
    /**
     * 指定した日付の全従業員のシフト希望を取得する。
     *
     * @param date 対象日付
     * @return 該当するシフト希望の一覧
     */
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
