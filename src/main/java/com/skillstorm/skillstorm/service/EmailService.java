package com.skillstorm.skillstorm.service;

import com.skillstorm.skillstorm.exceptions.InvalidEmailException;
import org.springframework.stereotype.Service;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.Record;
@Service
public class EmailService {

    public boolean hasMXRecord(String email){
        try{
            String domain = email.substring(email.indexOf("@") + 1);

            Lookup lookup = new Lookup(domain, Type.MX);
            Record[] records = lookup.run();

            return records.length > 1;
        } catch (InvalidEmailException | TextParseException e){
            return false;
        }
    }
}
