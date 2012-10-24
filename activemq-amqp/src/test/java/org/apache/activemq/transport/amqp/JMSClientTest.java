begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
operator|.
name|joram
operator|.
name|ActiveMQAdmin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|amqp_1_0
operator|.
name|jms
operator|.
name|impl
operator|.
name|ConnectionFactoryImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|amqp_1_0
operator|.
name|jms
operator|.
name|impl
operator|.
name|QueueImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
specifier|public
class|class
name|JMSClientTest
extends|extends
name|AmqpTestSupport
block|{
annotation|@
name|Test
specifier|public
name|void
name|testTransactions
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQAdmin
operator|.
name|enableJMSFrameTracing
argument_list|()
expr_stmt|;
name|QueueImpl
name|queue
init|=
operator|new
name|QueueImpl
argument_list|(
literal|"queue://txqueue"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|p
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello World"
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setObjectProperty
argument_list|(
literal|"x"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|p
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|//            session.commit();
name|MessageConsumer
name|c
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|,
literal|"x = 1"
argument_list|)
decl_stmt|;
name|Message
name|received
init|=
name|c
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"first: "
operator|+
operator|(
operator|(
name|TextMessage
operator|)
name|received
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|received
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
comment|//            session.rollback();
comment|//
comment|//            msg = c.receive();
comment|//            System.out.println("second:"+msg);
comment|//            System.out.println(msg.getJMSRedelivered());
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//    @Test
comment|//    public void testSendReceive() throws Exception {
comment|//        ActiveMQAdmin.enableJMSFrameTracing();
comment|//        QueueImpl queue = new QueueImpl("queue://testqueue");
comment|//        int nMsgs = 1;
comment|//        final String dataFormat = "%01024d";
comment|//
comment|//
comment|//        try {
comment|//            Connection connection = createConnection();
comment|//            {
comment|//                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//                MessageProducer p = session.createProducer(queue);
comment|//                for (int i = 0; i< nMsgs; i++) {
comment|//                    System.out.println("Sending " + i);
comment|//                    p.send(session.createTextMessage(String.format(dataFormat, i)));
comment|//                }
comment|//            }
comment|//            connection.close();
comment|//
comment|//            System.out.println("=======================================================================================");
comment|//            System.out.println(" failing a receive ");
comment|//            System.out.println("=======================================================================================");
comment|//            connection = createConnection();
comment|//            {
comment|//                Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
comment|//                MessageConsumer c = session.createConsumer(queue);
comment|//
comment|//                // Receive messages non-transacted
comment|//                int i = 0;
comment|//                while ( i< 1) {
comment|//                    TextMessage msg = (TextMessage) c.receive();
comment|//                    if( msg!=null ) {
comment|//                        String s = msg.getText();
comment|//                        assertEquals(String.format(dataFormat, i), s);
comment|//                        System.out.println("Received: " + i);
comment|//                        i++;
comment|//                    }
comment|//                }
comment|//            }
comment|//            connection.close();
comment|//
comment|//
comment|//            System.out.println("=======================================================================================");
comment|//            System.out.println(" receiving ");
comment|//            System.out.println("=======================================================================================");
comment|//            connection = createConnection();
comment|//            {
comment|//                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
comment|//                MessageConsumer c = session.createConsumer(queue);
comment|//
comment|//                // Receive messages non-transacted
comment|//                int i = 0;
comment|//                while ( i< nMsgs) {
comment|//                    TextMessage msg = (TextMessage) c.receive();
comment|//                    if( msg!=null ) {
comment|//                        String s = msg.getText();
comment|//                        assertEquals(String.format(dataFormat, i), s);
comment|//                        System.out.println("Received: " + i);
comment|//                        i++;
comment|//                    }
comment|//                }
comment|//            }
comment|//            connection.close();
comment|//
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//        }
comment|//
comment|//    }
specifier|private
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|JMSException
block|{
specifier|final
name|ConnectionFactoryImpl
name|factory
init|=
operator|new
name|ConnectionFactoryImpl
argument_list|(
literal|"localhost"
argument_list|,
name|port
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setExceptionListener
argument_list|(
operator|new
name|ExceptionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
name|exception
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
end_class

end_unit

