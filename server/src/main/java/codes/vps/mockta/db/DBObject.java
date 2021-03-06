/*
 * Copyright (c) 2022 Pawel S. Veselov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package codes.vps.mockta.db;

import java.io.Closeable;

public class DBObject implements Closeable {

    private final Object lock = new Object();
    private volatile boolean locked;

    public void checkOut() {
        synchronized (lock) {
            while (locked) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {}
            }
        }
    }

    @Override
    public void close() {
        synchronized (lock) {
            locked = false;
            lock.notifyAll();
        }
    }

}
