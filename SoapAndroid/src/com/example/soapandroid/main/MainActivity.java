package com.example.soapandroid.main;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soapandroid.R;

public class MainActivity extends Activity {

	private static String SOAP_ACTION = "http://www.w3schools.com/webservices/CelsiusToFahrenheit";
	private static String NAMESPACE = "http://www.w3schools.com/webservices/";
	private static String METHOD_NAME = "CelsiusToFahrenheit";
	private static String URLWSDL = "http://www.w3schools.com/webservices/tempconvert.asmx?WSDL";
	private static String URL = "http://www.w3schools.com/webservices/tempconvert.asmx";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final EditText editTextSend = (EditText) findViewById(R.id.editTextSend);
		final TextView textResponse = (TextView) findViewById(R.id.textResponse);
		Button btSendKsoap = (Button) findViewById(R.id.btSendKsoap);
		btSendKsoap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String textToSend = editTextSend.getText().toString();
				// Criacao do SoapObject que vai tratar dos parametros de
				// requisicao, deve ser especifico para cada metodo do
				// webservice
				Log.d("Criacao do SoapObject", "Namespace: " + NAMESPACE
						+ ", method name: " + METHOD_NAME);
				SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
				Log.d("Adicao do parametro de requisicao ", textToSend);
				request.addProperty("Celsius", textToSend);

				// Objeto que, conforme o nome diz, ira realizar o envio do
				// objeto de requisicao utilizando a versao do SOAP especificada
				Log.d("Criacao do SoapSerializationEnvelope", "versao 1.1");
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				// Informado qual eh o objeto de saida
				envelope.setOutputSoapObject(request);
				// Atributo que determina que deve haver compatibilidade com o
				// encode padrao para webservices .net
				Log.d("Encode ", " Compatibilidade com .net ");
				envelope.dotNet = true;
				try {
					// Objeto que ira enviar o envelope, contendo o objeto de
					// request para a url informada
					Log.d("Criacao do objeto HttpTransportSE ", URLWSDL);
					HttpTransportSE httpTransport = new HttpTransportSE(URLWSDL);
					// Faz a chamada efetiva do webservice, sendo necessario
					// informar qual vai ser o servico chamado
					Log.d("Chamada do webservice ", SOAP_ACTION);
					httpTransport.call(SOAP_ACTION, envelope);
					// Caso nao retorne nenhuma excecao, envelope tem seu
					// atributo bodyIn preenchido com a resposta da chamada
					Log.d("Recuperacao do resultado ", SOAP_ACTION);
					SoapObject result = (SoapObject) envelope.bodyIn;
					if (result != null) {
						Log.d("Recuperacao do resultado ", result
								.getProperty(0).toString());
						textResponse.setText(result.getProperty(0).toString());
					} else {
						Toast.makeText(getApplicationContext(), "No Response",
								Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {
					Log.e("Erro ao realizar chamada webservice",
							e.getMessage(), e);
					Toast.makeText(getApplicationContext(),
							"Erro ao realizar chamada webservice",
							Toast.LENGTH_LONG).show();

				}
			}
		});
		Button btSendNativo = (Button) findViewById(R.id.btSendNativo);
		btSendNativo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("Criacao do HttpPost", "URL: " + URL);
				// Criacao do Objeto HttpPost, para a url
				HttpPost httppost = new HttpPost(URL);
				StringEntity se;
				try {
					Log.d("Criacao do XML para envio", "NAMESPACE: "
							+ NAMESPACE);
					// Criacao do XML de envio
					String textToSend = editTextSend.getText().toString();
					StringBuilder requestXML = new StringBuilder(
							"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">")
							.append("<soap:Header/>").append("<soap:Body>")
							.append("<CelsiusToFahrenheit xmlns=\"")
							.append(NAMESPACE).append("\">")
							.append("<Celsius>").append(textToSend)
							.append("</Celsius>")
							.append("</CelsiusToFahrenheit>")
							.append("</soap:Body>").append("</soap:Envelope>");
					Log.d("Encode ", " encode do xml para UTF-8");
					// Encode do XML para o formato aceito pelo webservice
					se = new StringEntity(requestXML.toString(), HTTP.UTF_8);

					se.setContentType("text/xml");
					Log.d("Cabecalho ",
							" criacao do cabecalho com as informacoes basicas");
					// Cabecalho da requisicao, importante que deve-se setar os
					// Accept`s
					httppost.setHeader("Accept-Charset", "utf-8");
					httppost.setHeader(
							"Accept",
							"text/xml,application/text+xml,application/soap+xml,application/x-www-form-urlencoded");
					httppost.setHeader("Content-Type",
							"text/xml; charset=utf-8");
					httppost.setEntity(se);

					Log.d("Criacao do objeto HttpClient",
							" criacao do HttpClient para envio da requisicao");
					HttpClient httpclient = new DefaultHttpClient();
					Log.d("Chamada do webservice",
							" transferencia da resposta para o BasicHttpResponse");
					// Execucao do post e envio para o BasicHttpResponse
					BasicHttpResponse httpResponse = (BasicHttpResponse) httpclient
							.execute(httppost);
					HttpEntity resEntity = httpResponse.getEntity();
					// Conversao da entidade de resultado para string
					String res = EntityUtils.toString(resEntity);
					res = res.substring(
							res.indexOf("<CelsiusToFahrenheitResult>")
									+ "<CelsiusToFahrenheitResult>".length(),
							res.indexOf("</CelsiusToFahrenheitResult>"));
					textResponse.setText(res);

				} catch (Exception e) {
					Log.e("Erro ao realizar chamada webservice",
							e.getMessage(), e);
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
