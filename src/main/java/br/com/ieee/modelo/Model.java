package br.com.ieee.modelo;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Model {

    private String protocolVersion;
    private String type;
    private String toDS;
    private String fromDS;
    private String moreFragment;
    private String retry;
    private String powerManagement;
    private String moreData;
    private String wep;
    private String order;
    private String duration;
    private String transmitterAddress;
    private String destinationAddress;
    private String sourceAddress;
    private String receiverAddress;
    private String bssId;
    private String sequenceNumber;
    private String info;
    public Model() {
    }


}
