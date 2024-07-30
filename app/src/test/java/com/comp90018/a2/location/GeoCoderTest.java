package com.comp90018.a2.location;

import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GeoCoderTest extends TestCase {
    public void testGetLocation() {
        GeocodeService geoCoder = new GeocodeService();
        LatLong latlong = new LatLong(-37.7964, 144.9612);
        CountDownLatch latch = new CountDownLatch(1);


        Disposable subscribe = geoCoder.getAddress(latlong)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(address -> {
                    System.out.println("Address: " + address);
                }, throwable -> {
                    // Handle errors here
                    System.err.println("Error: " + throwable.getMessage());
                });

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            subscribe.dispose();
        }
    }
}