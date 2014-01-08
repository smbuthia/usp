/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usp.auth;

import com.google.gson.Gson;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JOptionPane;

/**
 *
 * @author smbuthia
 */
public class USPWriter {

    public void writeUSPReceipt(TicketInfo ticket) {
        Users users;
        String mobileNumber = JOptionPane.showInputDialog("Input a mobile phone number.");

        if (mobileNumber != null && !mobileNumber.equalsIgnoreCase("")) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("unicentaPU");

            if (emf.isOpen()) {
                EntityManager em = emf.createEntityManager();
                Query query = em.createNamedQuery("Users.findByMobile");
                query.setParameter("mobile", mobileNumber);
                users = (Users) query.getSingleResult();

                if (users.getEmailaddress() != null) {
                    Map map = new HashMap<>();
                    Gson gson = new Gson();
                    TicketLineInfo tLine;
                    String prodName;
                    String prodPrice;

                    for (int i = 0; i < ticket.getLinesCount(); i++) {
                        tLine = ticket.getLine(i);
                        prodName = tLine.getProductName();
                        prodPrice = Double.toString(tLine.getPrice());
                        map.put(prodName, prodPrice);
                    }
                    String receiptJsonString = gson.toJson(map);

                    em.getTransaction().begin();
                    Transactions transactions = new Transactions();
                    transactions.setReceipt(receiptJsonString);
                    transactions.setUserid(users.getEmailaddress());
                    transactions.setId(ticket.getId());
                    em.persist(transactions);
                    em.getTransaction().commit();
                }
                emf.close();
            }
        } else {
        }
    }
}
