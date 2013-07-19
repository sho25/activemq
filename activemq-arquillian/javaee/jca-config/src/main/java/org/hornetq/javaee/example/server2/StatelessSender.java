begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright 2009 Red Hat, Inc.  *  Red Hat licenses this file to you under the Apache License, version  *  2.0 (the "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or  *  implied.  See the License for the specific language governing  *  permissions and limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|hornetq
operator|.
name|javaee
operator|.
name|example
operator|.
name|server2
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|Remote
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|Stateless
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

begin_comment
comment|/**  * A Stateless Bean that will connect to a remote JBM.  *  * @author<a href="mailto:clebert.suconic@jboss.org">Clebert Suconic</a>  *  *  */
end_comment

begin_class
annotation|@
name|Remote
argument_list|(
name|StatelessSenderService
operator|.
name|class
argument_list|)
annotation|@
name|Stateless
specifier|public
class|class
name|StatelessSender
implements|implements
name|StatelessSenderService
block|{
comment|/**     *  Resource to be deployed by jms-remote-ds.xml     *  */
annotation|@
name|Resource
argument_list|(
name|mappedName
operator|=
literal|"java:/RemoteJmsXA"
argument_list|)
specifier|private
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
comment|/* (non-Javadoc)     * @see org.jboss.javaee.example.server.StatelessSenderService#sendHello(java.lang.String)     */
specifier|public
name|void
name|sendHello
parameter_list|(
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Step 4. Define the destinations that will receive the message (instead of using JNDI to the remote server)
comment|//Queue destQueueA = HornetQJMSClient.createQueue("A");
comment|//Queue destQueueB = HornetQJMSClient.createQueue("B");
comment|// Step 5. Create a connection to a remote server using a connection-factory (look at the deployed file jms-remote-ds.xml)
name|Connection
name|conn
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|(
literal|"guest"
argument_list|,
literal|"password"
argument_list|)
decl_stmt|;
comment|// Step 6. Send a message to a QueueA on the remote server, which will be received by MDBQueueA
name|Session
name|sess
init|=
name|conn
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
name|prodA
init|=
name|sess
operator|.
name|createProducer
argument_list|(
name|sess
operator|.
name|createQueue
argument_list|(
literal|"A"
argument_list|)
argument_list|)
decl_stmt|;
name|prodA
operator|.
name|send
argument_list|(
name|sess
operator|.
name|createTextMessage
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Step 7 (StatelessSender.java): Sent message \""
operator|+
name|message
operator|+
literal|"\" to QueueA"
argument_list|)
expr_stmt|;
comment|// Step 6. Send a message to a QueueB on the remote server, which will be received by MDBQueueA
name|MessageProducer
name|prodB
init|=
name|sess
operator|.
name|createProducer
argument_list|(
name|sess
operator|.
name|createQueue
argument_list|(
literal|"B"
argument_list|)
argument_list|)
decl_stmt|;
name|prodB
operator|.
name|send
argument_list|(
name|sess
operator|.
name|createTextMessage
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Step 8 (StatelessSender.java): Sent message \""
operator|+
name|message
operator|+
literal|"\" to QueueB"
argument_list|)
expr_stmt|;
comment|// Step 7. Close the connection. (Since this is a JCA connection, this will just place the connection back to a connection pool)
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Step 9 (StatelessSender.java): Closed Connection (sending it back to pool)"
argument_list|)
expr_stmt|;
block|}
comment|// Constants -----------------------------------------------------
comment|// Attributes ----------------------------------------------------
comment|// Static --------------------------------------------------------
comment|// Constructors --------------------------------------------------
comment|// Public --------------------------------------------------------
comment|// Package protected ---------------------------------------------
comment|// Protected -----------------------------------------------------
comment|// Private -------------------------------------------------------
comment|// Inner classes -------------------------------------------------
block|}
end_class

end_unit

