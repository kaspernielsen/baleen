/*
 * Copyright (c) 2008 Kasper Nielsen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.baleen.secom.serviceold;

import org.grad.secom.core.models.SubscriptionNotificationObject;
import org.grad.secom.core.models.UploadObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.dma.baleen.secom.service.SecomServiceRegistryService;
import dk.dma.baleen.secom.spi.AuthenticatedMcpNode;
import dk.dma.baleen.secom.util.BaleenSecomClient;

/**
 *
 */
@Service
class SecomOutboxService {

    @Autowired
    SecomServiceRegistryService serviceRegistry;

    void sendTo(AuthenticatedMcpNode node, SecomOperationType operation, Object message) {
        BaleenSecomClient client = serviceRegistry.resolveMRN(node.mrn());
        operation.sendTo(message, client);
    }

    public enum SecomOperationType {
        SUBSCRIPTION_NOTIFICATION {
            @Override
            protected void sendTo(Object o, BaleenSecomClient service) {
                System.out.println("Sending totificateion");
                service.subscriptionNotification((SubscriptionNotificationObject) o);
                System.out.println("Sending totificateion Done");
            }
        },
        UPLOAD {
            @Override
            protected void sendTo(Object o, BaleenSecomClient service) {
                System.out.println("Uploading to " + service.uri());
                service.upload((UploadObject) o);
                System.out.println("Uploading completed");
            }
        };

        protected abstract void sendTo(Object o, BaleenSecomClient service);
    }
}
