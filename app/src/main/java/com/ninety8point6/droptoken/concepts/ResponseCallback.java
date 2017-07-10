package com.ninety8point6.droptoken.concepts;

/**
 *
 * @param <T>
 * @param <U>
 */
public interface ResponseCallback<T, U extends Throwable> {

    /**
     *
     * @param response
     */
    void onSuccess(T response);

    /**
     *
     * @param reason
     */
    void onError(U reason);
}
