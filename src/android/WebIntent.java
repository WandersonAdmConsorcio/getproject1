package com.borismus.webintent;

import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CordovaActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.text.Html;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.PluginResult;

/**
 * WebIntent is a PhoneGap plugin that bridges Android intents and web
 * applications:
 * 
 * 1. web apps can spawn intents that call native Android applications. 2.
 * (after setting up correct intent filters for PhoneGap applications), Android
 * intents can be handled by PhoneGap web applications.
 * 
 * @author boris@borismus.com
 * 
 */
public class WebIntent extends CordovaPlugin {

    private CallbackContext onNewIntentCallbackContext = null;
	private CordovaPlugin activityResultCallback;
    private CallbackContext callbackContextCurrent;
	
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        try {
			
			callbackContextCurrent = callbackContext;

            if (action.equals("startActivity")) {
				
				 System.out.println("TESTE INTENT 3");
				
                if (args.length() != 1) {
                    //return new PluginResult(PluginResult.Status.INVALID_ACTION);
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
                    return false;
                }

                // Parse the arguments
				final CordovaResourceApi resourceApi = webView.getResourceApi();
                JSONObject obj = args.getJSONObject(0);
                String type = obj.has("type") ? obj.getString("type") : null;
                Uri uri = obj.has("url") ? resourceApi.remapUri(Uri.parse(obj.getString("url"))) : null;
                JSONObject extras = obj.has("extras") ? obj.getJSONObject("extras") : null;
                
				/*
				Map<String, String> extrasMap = new HashMap<String, String>();

                // Populate the extras if any exist
                if (extras != null) {
                    JSONArray extraNames = extras.names();
                    for (int i = 0; i < extraNames.length(); i++) {
						
                        String key = extraNames.getString(i);
                        String value = extras.getString(key);
						
						System.out.println("VALUE " + value + " KEY " + key);
						
                        extrasMap.put(key, value);
                    }
                }
				*/
				
				
                System.out.println("TESTE INTENT 4");
                //startActivity(obj.getString("action"), uri, type, extrasMap);
				startActivity(obj.getString("action"), uri, type, extras);
				
				
				PluginResult result = new PluginResult(PluginResult.Status.OK);
				result.setKeepCallback(true);
				
                return true;

            } else if (action.equals("hasExtra")) {
                if (args.length() != 1) {
                    //return new PluginResult(PluginResult.Status.INVALID_ACTION);
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
                    return false;
                }
                Intent i = ((CordovaActivity)this.cordova.getActivity()).getIntent();
                String extraName = args.getString(0);
                //return new PluginResult(PluginResult.Status.OK, i.hasExtra(extraName));
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, i.hasExtra(extraName)));
                return true;

            } else if (action.equals("getExtra")) {
                if (args.length() != 1) {
                    //return new PluginResult(PluginResult.Status.INVALID_ACTION);
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
                    return false;
                }
                Intent i = ((CordovaActivity)this.cordova.getActivity()).getIntent();
                String extraName = args.getString(0);
                if (i.hasExtra(extraName)) {
                    //return new PluginResult(PluginResult.Status.OK, i.getStringExtra(extraName));
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, i.getStringExtra(extraName)));
                    return true;
                } else {
                    //return new PluginResult(PluginResult.Status.ERROR);
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
                    return false;
                }
            } else if (action.equals("getUri")) {
                if (args.length() != 0) {
                    //return new PluginResult(PluginResult.Status.INVALID_ACTION);
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
                    return false;
                }

                Intent i = ((CordovaActivity)this.cordova.getActivity()).getIntent();
                String uri = i.getDataString();
                //return new PluginResult(PluginResult.Status.OK, uri);
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, uri));
                return true;
            } else if (action.equals("onNewIntent")) {
            	//save reference to the callback; will be called on "new intent" events
                this.onNewIntentCallbackContext = callbackContext;
        
                if (args.length() != 0) {
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
                    return false;
                }
                
                PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
                result.setKeepCallback(true); //re-use the callback on intent events
                callbackContext.sendPluginResult(result);
                return true;
                //return result;
            } else if (action.equals("sendBroadcast")) 
            {
                if (args.length() != 1) {
                    //return new PluginResult(PluginResult.Status.INVALID_ACTION);
                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
                    return false;
                }

                // Parse the arguments
                JSONObject obj = args.getJSONObject(0);

                JSONObject extras = obj.has("extras") ? obj.getJSONObject("extras") : null;
                Map<String, String> extrasMap = new HashMap<String, String>();

                // Populate the extras if any exist
                if (extras != null) {
                    JSONArray extraNames = extras.names();
                    for (int i = 0; i < extraNames.length(); i++) {
                        String key = extraNames.getString(i);
                        String value = extras.getString(key);
                        extrasMap.put(key, value);
                    }
                }

                sendBroadcast(obj.getString("action"), extrasMap);
                //return new PluginResult(PluginResult.Status.OK);
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));
                return true;
            }
            //return new PluginResult(PluginResult.Status.INVALID_ACTION);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            String errorMessage=e.getMessage();
            //return new PluginResult(PluginResult.Status.JSON_EXCEPTION);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION,errorMessage));
            return false;
        }
    }
	
	private static final int POS_RETORNO = 0;
	private static final int POS_CODIGO_ERRO = 1;
	private static final int POS_DISPLAY = 2;
	private static final int POS_VALOR = 3;
	private static final int POS_TOKEN = 4;
	private static final int POS_CONFIRMACAO_AUTOMATICA = 5;
	private static final int POS_CONFIRMACAO_REMOTA = 6;
	private static final int POS_ID_CONFIRMACAO = 7;
	private static final int POS_NSU_CTF = 8;	
	private static final int POS_NSU_AUTORIZADORA = 9;
	private static final int POS_CODIGO_APROVACAO = 10;
	private static final int POS_CODIGO_RESPOSTA_AUTORIZADORA = 11;
	private static final int POS_CARTAO = 12;
	private static final int POS_BANDEIRA = 13;
	private static final int POS_CUPOM_REDUZIDO = 14;
	private static final int POS_CUPOM_CLIENTE = 15;
	private static final int POS_CUPOM_ESTABELECIMENTO = 16;
	private static final int POS_DATA_HORA = 17;
	private static final int POS_CODIGO_AUTORIZADORA = 18;
	private static final int POS_TERMINAL_ORIGINAL = 19;
	
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		 
        if (requestCode == 1000) {
			
            if (resultCode == -1) {
				
				System.out.println("== -1");
				
				int codigoRetorno = intent.getIntExtra("br.com.auttar.mobile.ctfclient.intent.extra.COD_RETORNO", -1);
				String codigoErro = intent.getStringExtra("br.com.auttar.mobile.ctfclient.intent.extra.COD_ERRO");
				String[] display =  intent.getStringArrayExtra("br.com.auttar.mobile.ctfclient.intent.extra.DISPLAY");
				String valor = intent.getStringExtra("br.com.auttar.mobile.ctfclient.intent.extra.VALOR");
				String token = intent.getStringExtra("br.com.auttar.mobile.ctfclient.intent.extra.TOKEN");
				boolean confirmacaoAutomatica = intent.getBooleanExtra("br.com.auttar.mobile.ctfclient.intent.extra.CONFIRMACAO_AUTOMATICA", false);
				boolean confirmacaoRemota = intent.getBooleanExtra("br.com.auttar.mobile.ctfclient.intent.extra.CONFIRMACAO_REMOTA", false);
				long idConfirmacao = intent.getLongExtra("br.com.auttar.mobile.ctfclient.intent.extra.ID", -1);
				int nsuCTF = intent.getIntExtra("br.com.auttar.mobile.ctfclient.intent.extra.NSU_CTF", -1);
				long nsuAutorizadora = intent.getLongExtra("br.com.auttar.mobile.ctfclient.intent.extra.NSU_AUTORIZADORA", -1);
				String codigoAprovacao = intent.getStringExtra("br.com.auttar.mobile.ctfclient.intent.extra.COD_APROVACAO");
				String codigoRespostaAutorizadora = intent.getStringExtra("br.com.auttar.mobile.ctfclient.intent.extra.COD_RESP_AUTORIZADORA");
				String cartao = intent.getStringExtra("br.com.auttar.mobile.ctfclient.intent.extra.CARTAO");
				String bandeira = intent.getStringExtra("br.com.auttar.mobile.ctfclient.intent.extra.BANDEIRA");
				String[] cupomReduzido = intent.getStringArrayExtra("br.com.auttar.mobile.ctfclient.intent.extra.CUPOM_REDUZIDO");
				String[] cupomCliente = intent.getStringArrayExtra("br.com.auttar.mobile.ctfclient.intent.extra.CUPOM_CLIENTE");
				String[] cupomEstabelecimento = intent.getStringArrayExtra("br.com.auttar.mobile.ctfclient.intent.extra.CUPOM_ESTABELECIMENTO");
				String dataHora = intent.getStringExtra("br.com.auttar.mobile.ctfclient.intent.extra.DATA_HORA");
				String codigoAutorizadora = intent.getStringExtra("br.com.auttar.mobile.ctfclient.intent.extra.AUTORIZADORA");
				String terminalOriginal = intent.getStringExtra("br.com.auttar.mobile.ctfclient.intent.extra.TERMINAL");
				
				
				
				
				
				System.out.println("retorno: " + codigoRetorno + ", erro: " + codigoErro + ", display: " + display);
				
				JSONArray arrayRetorno = new JSONArray();
				
				try { 
					arrayRetorno.put(POS_RETORNO, codigoRetorno);
					arrayRetorno.put(POS_CODIGO_ERRO, codigoErro);
					arrayRetorno.put(POS_DISPLAY, converteArrayString(display));
					arrayRetorno.put(POS_VALOR, valor);
					arrayRetorno.put(POS_TOKEN, token);
					arrayRetorno.put(POS_CONFIRMACAO_AUTOMATICA, confirmacaoAutomatica);
					arrayRetorno.put(POS_CONFIRMACAO_REMOTA, confirmacaoRemota);
					arrayRetorno.put(POS_ID_CONFIRMACAO, idConfirmacao);
					arrayRetorno.put(POS_NSU_CTF, nsuCTF);
					arrayRetorno.put(POS_NSU_AUTORIZADORA, nsuAutorizadora);
					arrayRetorno.put(POS_CODIGO_APROVACAO, codigoAprovacao);
					arrayRetorno.put(POS_CODIGO_RESPOSTA_AUTORIZADORA, codigoRespostaAutorizadora);
					arrayRetorno.put(POS_CARTAO, cartao);
					arrayRetorno.put(POS_BANDEIRA, bandeira);
					arrayRetorno.put(POS_CUPOM_REDUZIDO, converteArrayString(cupomReduzido));
					arrayRetorno.put(POS_CUPOM_CLIENTE, converteArrayString(cupomCliente));
					arrayRetorno.put(POS_CUPOM_ESTABELECIMENTO, converteArrayString(cupomEstabelecimento));
					arrayRetorno.put(POS_DATA_HORA, dataHora);
					arrayRetorno.put(POS_CODIGO_AUTORIZADORA, codigoAutorizadora);
					arrayRetorno.put(POS_TERMINAL_ORIGINAL, terminalOriginal);
					
				} catch (JSONException e) {
					System.out.println("Erro ao criar o objeto de retorno JSON: " + e.getMessage());
					e.printStackTrace();
					
				}
				
				callbackContextCurrent.sendPluginResult(new PluginResult(PluginResult.Status.OK, arrayRetorno));
				
				//callbackContextCurrent.sendPluginResult(new PluginResult(PluginResult.Status.OK, codigoErro));
				
				
            } else {
				System.out.println("!= -1" + resultCode);
                //this.error(new PluginResult(PluginResult.Status.ERROR), this.callback);
            }
        }
    }
	
	private String converteArrayString(String[] array) {
		System.out.println("Tamanho array: " + array.length);
		if (array != null) {
			String result = null;
			for (int i = 0; i < array.length; i++) {
				if (result != null) {
					System.out.println("linha+ " + array[i]);
					result = result + "@" + array[i];
				} else {
					System.out.println("primeira linha+ " + array[i]);
					result = array[i];
				}
			}
			return result;
		}
		return null;
	}
	
    @Override
    public void onNewIntent(Intent intent) {
    	 
        if (this.onNewIntentCallbackContext != null) {
        	PluginResult result = new PluginResult(PluginResult.Status.OK, intent.getDataString());
        	result.setKeepCallback(true);
            this.onNewIntentCallbackContext.sendPluginResult(result);
        }
    }

    //private void startActivity(String action, Uri uri, String type, Map<String, String> extras) {
	private void startActivity(String action, Uri uri, String type, JSONObject extras) {
        Intent intent = (uri != null ? new Intent(action, uri) : new Intent(action));
        
        if (type != null && uri != null) {
            intent.setDataAndType(uri, type); //Fix the crash problem with android 2.3.6
        } else {
            if (type != null) {
                intent.setType(type);
            }
        }
        
		if (extras != null) {
			JSONArray extraNames = extras.names();
			for (int i = 0; i < extraNames.length(); i++) {
				
				try {
					String key = extraNames.getString(i);
					Object value = extras.get(key);
					
					if (value.getClass() == Boolean.class) {
						intent.putExtra(key, ((Boolean) value).booleanValue());
						
					} else if (value.getClass() == String.class) {
						intent.putExtra(key, ((String) value));
						
					} else if (value.getClass() == Integer.class) {
						intent.putExtra(key, ((Integer) value).intValue());
						
					} else if (value.getClass() == Long.class) {
						intent.putExtra(key, ((Long) value).longValue());
						
					} else {
						System.out.println("Tipo de entrada não identificado, o abributo será descartado! KEY " + key + ", VALUE " + value);
					}
				} catch (JSONException e) {
					System.out.println("Erro ao criar o objeto de retorno JSON: " + e.getMessage());
					e.printStackTrace();
				}
				
				
				
			}
		}
		
		/*
        for (String key : extras.keySet()) {
            String value = extras.get(key);
			i.putExtra(key, value);
			
			/*
            // If type is text html, the extra text must sent as HTML
            if (key.equals(Intent.EXTRA_TEXT) && type.equals("text/html")) {
                i.putExtra(key, Html.fromHtml(value));
            } else if (key.equals(Intent.EXTRA_STREAM)) {
                // allowes sharing of images as attachments.
                // value in this case should be a URI of a file
				final CordovaResourceApi resourceApi = webView.getResourceApi();
                i.putExtra(key, resourceApi.remapUri(Uri.parse(value)));
            } else if (key.equals(Intent.EXTRA_EMAIL)) {
                // allows to add the email address of the receiver
                i.putExtra(Intent.EXTRA_EMAIL, new String[] { value });
            } else {
			########
              
            }
        }
		*/
		
		//i.putExtra("br.com.auttar.mobile.ctfclient.intent.extra.COD_TRANSACAO", 112);
		/*
		setOperation(intent, 112);
		setTransactionNumber(intent, 1);
		//setAutomaticConfirmation(intent, true);
		//setRemoteConfirmation(intent, false);
		setSDKVersion(intent, 2);
		setInstallments(intent, 1);
		setAmount(intent, "1000");
		*/
		
		this.cordova.startActivityForResult(this, intent, 1000);
		
    }
	
    void sendBroadcast(String action, Map<String, String> extras) {
        Intent intent = new Intent();
        intent.setAction(action);
        for (String key : extras.keySet()) {
            String value = extras.get(key);
            intent.putExtra(key, value);
        }

        ((CordovaActivity)this.cordova.getActivity()).sendBroadcast(intent);
    }
	
	
	private void setOperation(Intent i, int operation) {
        i.putExtra("br.com.auttar.mobile.ctfclient.intent.extra.COD_TRANSACAO", operation);
    }

    private void setTransactionNumber(Intent i, int transactionNumber) {
        i.putExtra("br.com.auttar.mobile.ctfclient.intent.extra.NUM_TRANS", transactionNumber);
    }
	
    private void setAutomaticConfirmation(Intent i, boolean automaticConfirmation) {
        i.putExtra("br.com.auttar.mobile.ctfclient.intent.extra.CONFIRMACAO_AUTOMATICA", automaticConfirmation);
    }

    private void setRemoteConfirmation(Intent i, boolean remoteConfirmation) {
        i.putExtra("br.com.auttar.mobile.ctfclient.intent.extra.CONFIRMACAO_REMOTA", remoteConfirmation);
    }
    private void setSDKVersion(Intent i, int sdkVersion) {
        i.putExtra("br.com.auttar.mobile.ctfclient.intent.extra.VERSAO_SDK", sdkVersion);
    }

    private void setInstallments(Intent i, int installments) {
        i.putExtra("br.com.auttar.mobile.ctfclient.intent.extra.PARCELAS", installments);
    }

    private void setAmount(Intent i, String amount) {
        i.putExtra("br.com.auttar.mobile.ctfclient.intent.extra.VALOR", amount);
    }
}
