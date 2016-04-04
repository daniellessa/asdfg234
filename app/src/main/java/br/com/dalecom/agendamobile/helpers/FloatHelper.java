package br.com.dalecom.agendamobile.helpers;

import java.text.DecimalFormat;

/**
 * Created by daniellessa on 25/03/16.
 */
public class FloatHelper {

    public static String formatarFloat(float numero){
        String retorno = "";
        DecimalFormat formatter = new DecimalFormat("#.00");
        try{
            retorno = formatter.format(numero);
            retorno = retorno.replace(".",",");
        }catch(Exception ex){
            System.err.println("Erro ao formatar numero: " + ex);
        }
        return retorno;
    }
}
