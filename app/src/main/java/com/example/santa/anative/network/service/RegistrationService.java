package com.example.santa.anative.network.service;

import android.os.AsyncTask;
import android.util.Base64;

import com.example.santa.anative.application.Configurations;
import com.example.santa.anative.network.connection.Connection;
import com.example.santa.anative.network.connection.ConnectionDelegate;
import com.example.santa.anative.network.common.Observer;
import com.example.santa.anative.network.common.Service;
import com.example.santa.anative.util.algorithm.KeyCrypter;
import com.example.santa.anative.util.algorithm.Xor;
import com.example.santa.anative.util.common.ByteArray;
import com.example.santa.anative.util.common.ByteHelper;
import com.example.santa.anative.util.network.ServiceError;
import com.example.santa.anative.util.algorithm.AlgorithmUtils;
import com.example.santa.anative.util.algorithm.HkdfSha1;
import com.example.santa.anative.util.common.Validate;

import java.nio.ByteBuffer;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by santa on 18.03.17.
 */

public class RegistrationService extends AsyncTask<String, Integer, Void> implements Service, ConnectionDelegate {

    private Connection mConnection;
    private Observer mObserver;

    private String deviceId;
    private String email;

    private byte[] password;
    private byte[] code;

    private boolean isCode;

    // Server markers
    private static final String SERVER_HELLO_MESSAGE = "H";

    // Registration
    private static final String SERVER_CODE_SUCCESS = "U";
    private static final String SERVER_CODE_FAILURE = "u";
    private static final String SERVER_EXIST_REFRESH = "M";
    private static final String SERVER_EXIST_FAILURE = "m";
    private static final String SERVER_REGISTRATION_FAILED = "w";
    private static final String SERVER_REGISTRATION_SUCCESS = "J";
    private static final String SERVER_INCORRECT_PASSWORD = "D";

    // Client markers
    private static final String CLIENT_REGISTRATION_REQUEST = "G";
    private static final String CLIENT_REGISTRATION_CONFIRM = "X";

    /**
     *  Constructor RegistrationService, используется при создании запроса к серверу на получение
     *  кода подтверждения регистрации.
     */
    public RegistrationService(Connection connection, String email, String device) {
        mConnection = connection;
        this.email = email;
        this.deviceId = device;
        mConnection.attachDelegate(this);
    }

    /**
     *  Constructor RegistrationService, используется при подтверждении кода подтверждения, выланного
     *  сервером и регистрации нового пользователя.
     */
    public RegistrationService(Connection connection, String email, String device, byte[] password, byte[] code) {
        this.isCode = true;
        mConnection = connection;
        this.password = password;
        this.code = code;
        this.email = email;
        this.deviceId = device;
        mConnection.attachDelegate(this);
    }

    /**
     *  Override from AsyncTask
     *  @see AsyncTask
     */
    @Override
    protected Void doInBackground(String... params) {
        mConnection.start();
        return null;
    }


    /**
     *  Override from AsyncTask
     *  @see AsyncTask
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mObserver != null) mObserver.onError(values[0]);
    }


    /**
     *  Override from AsyncTask
     *  @see AsyncTask
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mObserver != null) mObserver.onComplete();
    }


    /**
     *  Override from Service
     *  @see Service
     */
    @Override
    public void onStop() {
        if (!isCancelled()) cancel(true);
        mConnection.stopClient();
    }


    /**
     *  Override from Service
     *  @see Service
     */
    @Override
    public void onStart() {
        this.execute();
    }


    /**
     *  Override from Service
     *  @see Service
     */
    @Override
    public void onSubscribe(Observer observer) {
        mObserver = observer;
    }


    /**
     *  Override from ConnectionDelegate
     *  @see ConnectionDelegate
     */
    @Override
    public void messageReceived(byte[] response) {
        try {
            String[] sessionParams = new String(response).split("\\s+");
            switch (sessionParams[0]) {
                // SUCCESS RESPONSE
                case SERVER_HELLO_MESSAGE:
                    startRegistrationProtocol(sessionParams[1]);
                    break;
                case SERVER_EXIST_REFRESH:
                    mObserver.onComplete();
                    break;
                case SERVER_CODE_SUCCESS:
                    mObserver.onComplete();
                    break;
                case SERVER_REGISTRATION_SUCCESS:
                    mObserver.onComplete();
                    break;

                // FAILURE RESPONSE
                case SERVER_CODE_FAILURE:
                    publishProgress(ServiceError.ERROR_CODE_FAILURE);
                    break;
                case SERVER_EXIST_FAILURE:
                    publishProgress(ServiceError.ERROR_USER_EXIST);
                    break;
                case SERVER_REGISTRATION_FAILED:
                    publishProgress(ServiceError.ERROR_REGISTRATION_FAILED);
                    break;
                case SERVER_INCORRECT_PASSWORD:
                    publishProgress(ServiceError.ERROR_INCORRECT_PASSWORD);
                    break;
                default:
                    publishProgress(ServiceError.ERROR_RESPONSE);
                    break;
            }
        } catch (Exception exception) {
            publishProgress(ServiceError.ERROR_UNKNOWN);
        }
    }

    /**
     *
     * Данный метод проверяет имя сервера,
     * В случае инвалидности возвращает error, с кодом ошибки ERROR_SERVER_NAME
     * Иначе вызывает метод generateSessionRequest()
     * @param serverName имя сервера
     */
    private void startRegistrationProtocol(String serverName) {
        if (!Validate.isNullOrEmpty(serverName) && serverName.equals(Configurations.SERVER_NAME)) {
            String clientId = String.format(Locale.ENGLISH, "%s#%s", email, deviceId);
            if (isCode) {
                ByteArray response = new ByteArray();
                response.appendWithSplit(ByteHelper.SPACE,
                        CLIENT_REGISTRATION_REQUEST.getBytes(),
                        clientId.getBytes(),
                        AlgorithmUtils.generateAbilitiesMask())
                        .append(ByteHelper.NL);
                mConnection.sendMessage(response.array());
            } else {
                ByteArray block = new ByteArray();
                block.append(code)
                        .append(ByteHelper.COMMA)
                        .append(ByteHelper.NULL)
                        .append(ByteHelper.COMMA)
                        .append(ByteHelper.intToByteArray(password.length))
                        .append(ByteHelper.COMMA)
                        .append(password)
                        .append(ByteHelper.COMMA)
                        .fillFreeRandom(180);

                byte[] key = HkdfSha1.deriveKey(code,
                        clientId.getBytes(),
                        Configurations.SERVER_NAME.getBytes(),
                        128);

                byte[] encode = KeyCrypter.encode(key, block.array());

                ByteArray response = new ByteArray();
                response.appendWithSplit(ByteHelper.SPACE,
                        CLIENT_REGISTRATION_CONFIRM.getBytes(),
                        clientId.getBytes(),
                        AlgorithmUtils.generateAbilitiesMask(),
                        Base64.encode(encode, Base64.DEFAULT))
                        .append(ByteHelper.NL);
                mConnection.sendMessage(response.array());

                // Arrays.fill(password, (byte) 32);
                // Arrays.fill(encode, (byte) 32);
                // Arrays.fill(key, (byte) 32);
                // block.reset();
            }
        } else {
            publishProgress(ServiceError.ERROR_RESPONSE);
        }
    }


    /**
     *
     * @param marker маркер запроса
     * @param email email пользователя для регистрации
     * @param deviceId уникальный идентификатор устройства
     * @param mask маска возможностей клиента
     * @return строка запроса
     */
    private String generateRegistrationRequest(String marker, String email, String deviceId, String mask) {
        Formatter request = new Formatter();
        request.format(Locale.ENGLISH, "%s %s#%s %s%n", marker, email, deviceId, mask);
        return request.toString();
    }


    /**
     *
     * @param marker маркер запроса
     * @param email email пользователя для регистрации
     * @param deviceId уникальный идентификатор устройства
     * @param mask маска возможностей клиента
     * @return строка запроса
     */
    private String generateConfirmRequest(String marker, String email, String deviceId, String mask, String block) {
        Formatter request = new Formatter();
        request.format(Locale.ENGLISH, "%s %s#%s %s %s%n", marker, email, deviceId, mask, block);
        return request.toString();
    }
}
