begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Protique Ltd  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|usecases
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ExceptionListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * @author Oliver Belikan  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ExceptionListenerTest
extends|extends
name|TestCase
implements|implements
name|ExceptionListener
block|{
name|boolean
name|isException
init|=
literal|false
decl_stmt|;
specifier|public
name|ExceptionListenerTest
parameter_list|(
name|String
name|arg
parameter_list|)
block|{
name|super
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOnException
parameter_list|()
throws|throws
name|Exception
block|{
comment|/* TODO not sure yet if this is a valid test          System.setProperty("activemq.persistenceAdapter",                 "org.activemq.store.vm.VMPersistenceAdapter");         // configuration of container and all protocolls         BrokerContainerImpl container = new                 BrokerContainerImpl("DefaultBroker");         BrokerConnectorImpl connector = new                 BrokerConnectorImpl(container,                         "vm://localhost", new DefaultWireFormat());         container.start();          ActiveMQConnectionFactory factory = new                 ActiveMQConnectionFactory("vm://localhost");         factory.start();          Connection connection = factory.createConnection();         connection.setExceptionListener(this);         connection.start();         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);         Destination destination = session.createTopic(getClass().getName());         MessageProducer producer = session.createProducer(destination);          try {             Thread.currentThread().sleep(1000);         }         catch (Exception e) {         }          container.stop();          // now lets try send         try {             producer.send(session.createTextMessage("This will never get anywhere"));         }         catch (JMSException e) {             System.out.println("Caught: " + e);         }          try {             Thread.currentThread().sleep(1000);         }         catch (Exception e) {         }          assertTrue("Should have received an exception", isException);         */
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|isException
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

