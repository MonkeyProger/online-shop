package org.scenter.onlineshop.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Data
@NoArgsConstructor
public class ResponseFile {
    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;
    private String fileDBid;
    private String name;
    private String url;
    private String type;
    private int size;

    public ResponseFile(String fileDBid, String name, String url, String type, int size){
        this.fileDBid = fileDBid;
        this.name = name;
        this.url = url;
        this.type = type;
        this.size = size;
    }
}
