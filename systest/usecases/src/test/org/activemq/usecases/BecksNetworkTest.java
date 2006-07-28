begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerContainer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|broker
operator|.
name|impl
operator|.
name|BrokerContainerImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|store
operator|.
name|vm
operator|.
name|VMPersistenceAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_comment
comment|/**  * @author bbeck  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|BecksNetworkTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BecksNetworkTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_BROKERS
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_PRODUCERS
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_CONSUMERS
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|NUM_MESSAGES
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MESSAGE_SEND_DELAY
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MESSAGE_RECEIVE_DELAY
init|=
literal|50
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|BASE_PORT
init|=
literal|9500
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"QUEUE"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MESSAGE_PRODUCER_KEY
init|=
literal|"PRODUCER"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MESSAGE_BODY_KEY
init|=
literal|"BODY"
decl_stmt|;
specifier|public
name|void
name|testCase
parameter_list|()
throws|throws
name|Throwable
block|{
name|main
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|String
index|[]
name|addresses
init|=
operator|new
name|String
index|[
name|NUM_BROKERS
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_BROKERS
condition|;
name|i
operator|++
control|)
block|{
name|addresses
index|[
name|i
index|]
operator|=
literal|"tcp://localhost:"
operator|+
operator|(
name|BASE_PORT
operator|+
name|i
operator|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Starting brokers"
argument_list|)
expr_stmt|;
name|BrokerContainer
index|[]
name|brokers
init|=
name|startBrokers
argument_list|(
name|addresses
argument_list|)
decl_stmt|;
name|String
name|reliableURL
init|=
name|createAddressString
argument_list|(
name|addresses
argument_list|,
literal|"reliable:"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Creating simulation state"
argument_list|)
expr_stmt|;
specifier|final
name|SimulationState
name|state
init|=
operator|new
name|SimulationState
argument_list|(
name|NUM_PRODUCERS
operator|*
name|NUM_MESSAGES
argument_list|)
decl_stmt|;
name|Thread
name|stateWatcher
init|=
operator|new
name|Thread
argument_list|(
literal|"Simulation State Watcher Thread"
argument_list|)
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|state
operator|.
name|getState
argument_list|()
operator|!=
name|SimulationState
operator|.
name|FINISHED
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"State: "
operator|+
name|state
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|wait
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{                         }
block|}
block|}
block|}
block|}
decl_stmt|;
name|stateWatcher
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|stateWatcher
operator|.
name|start
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Starting components"
argument_list|)
expr_stmt|;
name|MessageProducerComponent
index|[]
name|producers
init|=
operator|new
name|MessageProducerComponent
index|[
name|NUM_PRODUCERS
index|]
decl_stmt|;
name|MessageConsumerComponent
index|[]
name|consumers
init|=
operator|new
name|MessageConsumerComponent
index|[
name|NUM_CONSUMERS
index|]
decl_stmt|;
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_PRODUCERS
condition|;
name|i
operator|++
control|)
block|{
name|producers
index|[
name|i
index|]
operator|=
operator|new
name|MessageProducerComponent
argument_list|(
name|state
argument_list|,
literal|"MessageProducer["
operator|+
name|i
operator|+
literal|"]"
argument_list|,
name|reliableURL
argument_list|,
name|NUM_MESSAGES
argument_list|)
expr_stmt|;
name|producers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_CONSUMERS
condition|;
name|i
operator|++
control|)
block|{
name|consumers
index|[
name|i
index|]
operator|=
operator|new
name|MessageConsumerComponent
argument_list|(
name|state
argument_list|,
literal|"MessageConsumer["
operator|+
name|i
operator|+
literal|"]"
argument_list|,
name|reliableURL
argument_list|)
expr_stmt|;
name|consumers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Start the simulation
name|log
operator|.
name|info
argument_list|(
literal|"##### Starting the simulation..."
argument_list|)
expr_stmt|;
name|state
operator|.
name|setState
argument_list|(
name|SimulationState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|producers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|producers
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Producers finished"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|consumers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|consumers
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|consumers
index|[
name|i
index|]
operator|.
name|getId
argument_list|()
operator|+
literal|" consumed "
operator|+
name|consumers
index|[
name|i
index|]
operator|.
name|getNumberOfMessagesConsumed
argument_list|()
operator|+
literal|" messages."
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Consumers finished"
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"State: "
operator|+
name|state
argument_list|)
expr_stmt|;
name|state
operator|.
name|waitForSimulationState
argument_list|(
name|SimulationState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Stopping brokers"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|brokers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|brokers
index|[
name|i
index|]
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|BrokerContainer
index|[]
name|startBrokers
parameter_list|(
name|String
index|[]
name|addresses
parameter_list|)
throws|throws
name|JMSException
block|{
name|BrokerContainer
index|[]
name|containers
init|=
operator|new
name|BrokerContainer
index|[
name|addresses
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|containers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|containers
index|[
name|i
index|]
operator|=
operator|new
name|BrokerContainerImpl
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|containers
index|[
name|i
index|]
operator|.
name|setPersistenceAdapter
argument_list|(
operator|new
name|VMPersistenceAdapter
argument_list|()
argument_list|)
expr_stmt|;
name|containers
index|[
name|i
index|]
operator|.
name|addConnector
argument_list|(
name|addresses
index|[
name|i
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|addresses
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
name|j
condition|)
block|{
continue|continue;
block|}
name|containers
index|[
name|i
index|]
operator|.
name|addNetworkConnector
argument_list|(
literal|"reliable:"
operator|+
name|addresses
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|containers
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Created broker on "
operator|+
name|addresses
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Delay so this broker has a chance to come up fully...
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
operator|*
name|containers
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{         }
return|return
name|containers
return|;
block|}
comment|/*     private static BrokerContainer[] startBrokers(String[] addresses) throws JMSException, IOException     {     for(int i = 0; i< addresses.length; i++) {                     File temp = File.createTempFile("broker_" + i + "_", ".xml");                     temp.deleteOnExit();                       PrintWriter fout = new PrintWriter(new FileWriter(temp));                     fout.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");                     fout.println("<!DOCTYPE beans PUBLIC  \"-//ACTIVEMQ//DTD//EN\" \"http://activemq.codehaus.org/dtd/activemq.dtd\">");                     fout.println("<beans>");                     fout.println("<broker name=\"" + "receiver" + i + "\">");                     fout.println("<connector>");                     fout.println("<tcpServerTransport uri=\"" + addresses[i] + "\"/>");                     fout.println("</connector>");                      if(addresses.length> 1) {                             String otherAddresses = createAddressString(addresses, "list:", addresses[i]);                             otherAddresses = "tcp://localhost:9000";              fout.println("<networkConnector>");             fout.println("<networkChannel uri=\"" + otherAddresses + "\"/>");             fout.println("</networkConnector>");                     }                      fout.println("<persistence>");                     fout.println("<vmPersistence/>");                     fout.println("</persistence>");                     fout.println("</broker>");                     fout.println("</beans>");                     fout.close();                      ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://" + i);                     factory.setUseEmbeddedBroker(true);                     factory.setBrokerXmlConfig("file:" + temp.getAbsolutePath());                     factory.setBrokerName("broker-" + addresses[i]);                     factory.start();                      Connection c = factory.createConnection();                     c.start();     }              // Delay so this broker has a chance to come up fully...             try { Thread.sleep(2000*addresses.length); }             catch(InterruptedException ignored) {}              return null;     }     */
specifier|private
specifier|static
name|String
name|createAddressString
parameter_list|(
name|String
index|[]
name|addresses
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|addressToSkip
parameter_list|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|addresses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|addressToSkip
operator|!=
literal|null
operator|&&
name|addressToSkip
operator|.
name|equals
argument_list|(
name|addresses
index|[
name|i
index|]
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|addresses
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|first
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|SimulationState
block|{
specifier|public
specifier|static
specifier|final
name|int
name|INITIALIZED
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|RUNNING
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|FINISHED
init|=
literal|3
decl_stmt|;
specifier|private
specifier|final
name|Object
name|m_stateLock
decl_stmt|;
specifier|private
name|int
name|m_state
decl_stmt|;
specifier|private
specifier|final
name|int
name|m_numExpectedMessages
decl_stmt|;
specifier|private
specifier|final
name|Set
name|m_messagesProduced
decl_stmt|;
specifier|private
specifier|final
name|Set
name|m_messagesConsumed
decl_stmt|;
specifier|public
name|SimulationState
parameter_list|(
name|int
name|numMessages
parameter_list|)
block|{
name|m_stateLock
operator|=
operator|new
name|Object
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|m_stateLock
init|)
block|{
name|m_state
operator|=
name|INITIALIZED
expr_stmt|;
name|m_numExpectedMessages
operator|=
name|numMessages
expr_stmt|;
name|m_messagesProduced
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
name|m_messagesConsumed
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getState
parameter_list|()
block|{
synchronized|synchronized
init|(
name|m_stateLock
init|)
block|{
return|return
name|m_state
return|;
block|}
block|}
specifier|public
name|void
name|setState
parameter_list|(
name|int
name|newState
parameter_list|)
block|{
synchronized|synchronized
init|(
name|m_stateLock
init|)
block|{
name|m_state
operator|=
name|newState
expr_stmt|;
name|m_stateLock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|waitForSimulationState
parameter_list|(
name|int
name|state
parameter_list|)
throws|throws
name|InterruptedException
block|{
synchronized|synchronized
init|(
name|m_stateLock
init|)
block|{
while|while
condition|(
name|m_state
operator|!=
name|state
condition|)
block|{
name|m_stateLock
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|onMessageProduced
parameter_list|(
name|String
name|producerId
parameter_list|,
name|String
name|messageBody
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"-> onMessageProduced("
operator|+
name|producerId
operator|+
literal|", "
operator|+
name|messageBody
operator|+
literal|")"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|m_stateLock
init|)
block|{
if|if
condition|(
name|m_state
operator|==
name|INITIALIZED
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Message produced before the simulation has begun: messageBody="
operator|+
name|messageBody
argument_list|)
throw|;
block|}
if|if
condition|(
name|m_state
operator|==
name|FINISHED
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Message produced after the simulation has finished: messageBody="
operator|+
name|messageBody
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|m_messagesProduced
operator|.
name|add
argument_list|(
name|messageBody
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Duplicate message produced: messageBody="
operator|+
name|messageBody
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|onMessageConsumed
parameter_list|(
name|String
name|consumerId
parameter_list|,
name|String
name|messageBody
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"<- onMessageConsumed("
operator|+
name|consumerId
operator|+
literal|", "
operator|+
name|messageBody
operator|+
literal|")"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|m_stateLock
init|)
block|{
if|if
condition|(
name|m_state
operator|!=
name|RUNNING
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Message consumed while the simulation wasn't running: state = "
operator|+
name|m_state
operator|+
literal|", messageBody="
operator|+
name|messageBody
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|m_messagesProduced
operator|.
name|contains
argument_list|(
name|messageBody
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Message consumed that wasn't produced: messageBody="
operator|+
name|messageBody
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|m_messagesConsumed
operator|.
name|add
argument_list|(
name|messageBody
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Message consumed more than once: messageBody="
operator|+
name|messageBody
argument_list|)
throw|;
block|}
if|if
condition|(
name|m_messagesConsumed
operator|.
name|size
argument_list|()
operator|==
name|m_numExpectedMessages
condition|)
block|{
name|setState
argument_list|(
name|FINISHED
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"All expected messages have been consumed, finishing simulation."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
synchronized|synchronized
init|(
name|m_stateLock
init|)
block|{
name|SortedMap
name|unconsumed
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|m_messagesProduced
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|message
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|colonIndex
init|=
name|message
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|String
name|producerId
init|=
name|message
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colonIndex
argument_list|)
decl_stmt|;
name|Integer
name|numMessages
init|=
operator|(
name|Integer
operator|)
name|unconsumed
operator|.
name|get
argument_list|(
name|producerId
argument_list|)
decl_stmt|;
name|numMessages
operator|=
operator|(
name|numMessages
operator|==
literal|null
operator|)
condition|?
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
else|:
operator|new
name|Integer
argument_list|(
name|numMessages
operator|.
name|intValue
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
name|unconsumed
operator|.
name|put
argument_list|(
name|producerId
argument_list|,
name|numMessages
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|iter
init|=
name|m_messagesConsumed
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|message
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|colonIndex
init|=
name|message
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|String
name|producerId
init|=
name|message
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colonIndex
argument_list|)
decl_stmt|;
name|Integer
name|numMessages
init|=
operator|(
name|Integer
operator|)
name|unconsumed
operator|.
name|get
argument_list|(
name|producerId
argument_list|)
decl_stmt|;
name|numMessages
operator|=
operator|(
name|numMessages
operator|==
literal|null
operator|)
condition|?
operator|new
name|Integer
argument_list|(
operator|-
literal|1
argument_list|)
else|:
operator|new
name|Integer
argument_list|(
name|numMessages
operator|.
name|intValue
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|numMessages
operator|.
name|intValue
argument_list|()
operator|==
literal|0
condition|)
block|{
name|unconsumed
operator|.
name|remove
argument_list|(
name|producerId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|unconsumed
operator|.
name|put
argument_list|(
name|producerId
argument_list|,
name|numMessages
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|"SimulationState["
operator|+
literal|"state="
operator|+
name|m_state
operator|+
literal|" "
operator|+
literal|"numExpectedMessages="
operator|+
name|m_numExpectedMessages
operator|+
literal|" "
operator|+
literal|"numMessagesProduced="
operator|+
name|m_messagesProduced
operator|.
name|size
argument_list|()
operator|+
literal|" "
operator|+
literal|"numMessagesConsumed="
operator|+
name|m_messagesConsumed
operator|.
name|size
argument_list|()
operator|+
literal|" "
operator|+
literal|"unconsumed="
operator|+
name|unconsumed
return|;
block|}
block|}
block|}
specifier|private
specifier|static
specifier|abstract
class|class
name|SimulationComponent
extends|extends
name|Thread
block|{
specifier|protected
specifier|final
name|SimulationState
name|m_simulationState
decl_stmt|;
specifier|protected
specifier|final
name|String
name|m_id
decl_stmt|;
specifier|protected
specifier|abstract
name|void
name|_initialize
parameter_list|()
throws|throws
name|Throwable
function_decl|;
specifier|protected
specifier|abstract
name|void
name|_run
parameter_list|()
throws|throws
name|Throwable
function_decl|;
specifier|protected
specifier|abstract
name|void
name|_cleanup
parameter_list|()
throws|throws
name|Throwable
function_decl|;
specifier|public
name|SimulationComponent
parameter_list|(
name|SimulationState
name|state
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|m_simulationState
operator|=
name|state
expr_stmt|;
name|m_id
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|m_id
return|;
block|}
specifier|public
specifier|final
name|void
name|run
parameter_list|()
block|{
try|try
block|{
try|try
block|{
name|_initialize
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error during initialization"
argument_list|,
name|t
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
if|if
condition|(
name|m_simulationState
operator|.
name|getState
argument_list|()
operator|==
name|SimulationState
operator|.
name|FINISHED
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|m_id
operator|+
literal|" : NO NEED TO WAIT FOR RUNNING - already FINISHED"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
name|m_id
operator|+
literal|": WAITING for RUNNING started"
argument_list|)
expr_stmt|;
name|m_simulationState
operator|.
name|waitForSimulationState
argument_list|(
name|SimulationState
operator|.
name|RUNNING
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|m_id
operator|+
literal|": WAITING for RUNNING finished"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Interrupted during wait for the simulation to begin"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|_run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error during running"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|_cleanup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error during cleanup"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
specifier|static
specifier|abstract
class|class
name|JMSComponent
extends|extends
name|SimulationComponent
block|{
specifier|protected
specifier|final
name|String
name|m_url
decl_stmt|;
specifier|protected
name|Connection
name|m_connection
decl_stmt|;
specifier|protected
name|Session
name|m_session
decl_stmt|;
specifier|protected
name|Queue
name|m_queue
decl_stmt|;
specifier|public
name|JMSComponent
parameter_list|(
name|SimulationState
name|state
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|state
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|m_url
operator|=
name|url
expr_stmt|;
block|}
specifier|protected
name|void
name|_initialize
parameter_list|()
throws|throws
name|JMSException
block|{
name|m_connection
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|m_url
argument_list|)
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|m_connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|m_session
operator|=
name|m_connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|m_queue
operator|=
name|m_session
operator|.
name|createQueue
argument_list|(
name|QUEUE_NAME
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|_cleanup
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|m_session
operator|!=
literal|null
condition|)
block|{
name|m_session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|m_connection
operator|!=
literal|null
condition|)
block|{
name|m_connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|MessageProducerComponent
extends|extends
name|JMSComponent
block|{
specifier|private
specifier|final
name|int
name|m_numMessagesToSend
decl_stmt|;
specifier|private
name|MessageProducer
name|m_producer
decl_stmt|;
specifier|public
name|MessageProducerComponent
parameter_list|(
name|SimulationState
name|state
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|url
parameter_list|,
name|int
name|numMessages
parameter_list|)
block|{
name|super
argument_list|(
name|state
argument_list|,
name|id
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|m_numMessagesToSend
operator|=
name|numMessages
expr_stmt|;
block|}
specifier|protected
name|void
name|_initialize
parameter_list|()
throws|throws
name|JMSException
block|{
name|super
operator|.
name|_initialize
argument_list|()
expr_stmt|;
name|m_producer
operator|=
name|m_session
operator|.
name|createProducer
argument_list|(
name|m_queue
argument_list|)
expr_stmt|;
name|m_producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|_cleanup
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|m_producer
operator|!=
literal|null
condition|)
block|{
name|m_producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|_cleanup
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|_run
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|log
operator|.
name|debug
argument_list|(
name|m_id
operator|+
literal|": started"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|num
init|=
literal|0
init|;
name|num
operator|<
name|m_numMessagesToSend
condition|;
name|num
operator|++
control|)
block|{
name|String
name|messageBody
init|=
name|createMessageBody
argument_list|(
name|m_id
argument_list|,
name|num
argument_list|)
decl_stmt|;
name|MapMessage
name|message
init|=
name|m_session
operator|.
name|createMapMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setString
argument_list|(
name|MESSAGE_PRODUCER_KEY
argument_list|,
name|m_id
argument_list|)
expr_stmt|;
name|message
operator|.
name|setString
argument_list|(
name|MESSAGE_BODY_KEY
argument_list|,
name|messageBody
argument_list|)
expr_stmt|;
comment|// Pretend to be doing some work....
name|Thread
operator|.
name|sleep
argument_list|(
name|MESSAGE_SEND_DELAY
argument_list|)
expr_stmt|;
name|m_simulationState
operator|.
name|onMessageProduced
argument_list|(
name|m_id
argument_list|,
name|messageBody
argument_list|)
expr_stmt|;
name|m_producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|createMessageBody
parameter_list|(
name|String
name|id
parameter_list|,
name|int
name|num
parameter_list|)
block|{
return|return
name|id
operator|+
literal|":"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|num
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|MessageConsumerComponent
extends|extends
name|JMSComponent
implements|implements
name|MessageListener
block|{
specifier|private
specifier|final
name|Object
name|m_stateLock
decl_stmt|;
specifier|private
name|boolean
name|m_inOnMessage
decl_stmt|;
specifier|private
name|MessageConsumer
name|m_consumer
decl_stmt|;
specifier|private
name|int
name|m_numMessagesConsumed
decl_stmt|;
specifier|public
name|MessageConsumerComponent
parameter_list|(
name|SimulationState
name|state
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|state
argument_list|,
name|id
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|m_stateLock
operator|=
operator|new
name|Object
argument_list|()
expr_stmt|;
name|m_inOnMessage
operator|=
literal|false
expr_stmt|;
block|}
specifier|protected
name|void
name|_initialize
parameter_list|()
throws|throws
name|JMSException
block|{
name|super
operator|.
name|_initialize
argument_list|()
expr_stmt|;
name|m_consumer
operator|=
name|m_session
operator|.
name|createConsumer
argument_list|(
name|m_queue
argument_list|)
expr_stmt|;
name|m_consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|m_numMessagesConsumed
operator|=
literal|0
expr_stmt|;
block|}
specifier|protected
name|void
name|_cleanup
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|m_consumer
operator|!=
literal|null
condition|)
block|{
name|m_consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|_cleanup
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|_run
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|log
operator|.
name|info
argument_list|(
name|m_id
operator|+
literal|": WAITING for FINISHED started"
argument_list|)
expr_stmt|;
name|m_simulationState
operator|.
name|waitForSimulationState
argument_list|(
name|SimulationState
operator|.
name|FINISHED
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
name|m_id
operator|+
literal|": WAITING for FINISHED finished"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getNumberOfMessagesConsumed
parameter_list|()
block|{
return|return
name|m_numMessagesConsumed
return|;
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
synchronized|synchronized
init|(
name|m_stateLock
init|)
block|{
if|if
condition|(
name|m_inOnMessage
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Already in onMessage!!!"
argument_list|)
expr_stmt|;
block|}
name|m_inOnMessage
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
name|MapMessage
name|message
init|=
operator|(
name|MapMessage
operator|)
name|msg
decl_stmt|;
name|String
name|messageBody
init|=
name|message
operator|.
name|getString
argument_list|(
name|MESSAGE_BODY_KEY
argument_list|)
decl_stmt|;
name|m_simulationState
operator|.
name|onMessageConsumed
argument_list|(
name|m_id
argument_list|,
name|messageBody
argument_list|)
expr_stmt|;
name|m_numMessagesConsumed
operator|++
expr_stmt|;
comment|// Pretend to be doing some work....
name|Thread
operator|.
name|sleep
argument_list|(
name|MESSAGE_RECEIVE_DELAY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unexpected error during onMessage: message="
operator|+
name|msg
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|m_stateLock
init|)
block|{
if|if
condition|(
operator|!
name|m_inOnMessage
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Not already in onMessage!!!"
argument_list|)
expr_stmt|;
block|}
name|m_inOnMessage
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

