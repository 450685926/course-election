package com.server.edu.election;

import com.server.edu.election.dao.ElcBillDao;
import com.server.edu.election.entity.ElcBill;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("dev")
public class CultureRpcTest extends ApplicationTest {
    @Autowired
    private ElcBillDao elcBillDao;
    @Test
    public void test1() throws Exception {
        CultureSerivceInvoker.getCoursesLevel();
    }

    @Test
    public void test2(){
        List<ElcBill> elcBillList = new ArrayList<>();
        ElcBill elcBill = new ElcBill();
        elcBill.setBillNum("2");
        elcBill.setFlag(true);
        elcBillList.add(elcBill);
        ElcBill bill = new ElcBill();
        bill.setBillNum("3");
        bill.setFlag(false);
        elcBillList.add(bill);
        elcBillDao.updatePayBatch(elcBillList);
    }
    
}
