begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  Licensed to the Apache Software Foundation (ASF) under one or more  *  contributor license agreements.  See the NOTICE file distributed with  *  this work for additional information regarding copyright ownership.  *  The ASF licenses this file to You under the Apache License, Version 2.0  *  (the "License"); you may not use this file except in compliance with  *  the License.  You may obtain a copy of the License at  *  *  http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
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
name|javax
operator|.
name|jms
operator|.
name|MapMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_comment
comment|/**  * The Supplier synchronously receives the order from the Vendor and  * randomly responds with either the number ordered, or some lower  * quantity.   */
end_comment

begin_class
specifier|public
class|class
name|Supplier
implements|implements
name|Runnable
block|{
specifier|private
name|String
name|url
decl_stmt|;
specifier|private
name|String
name|user
decl_stmt|;
specifier|private
name|String
name|password
decl_stmt|;
specifier|private
specifier|final
name|String
name|ITEM
decl_stmt|;
specifier|private
specifier|final
name|String
name|QUEUE
decl_stmt|;
specifier|public
name|Supplier
parameter_list|(
name|String
name|item
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|ITEM
operator|=
name|item
expr_stmt|;
name|this
operator|.
name|QUEUE
operator|=
name|queue
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|user
argument_list|,
name|password
argument_list|,
name|url
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
literal|null
decl_stmt|;
name|Destination
name|orderQueue
decl_stmt|;
try|try
block|{
name|Connection
name|connection
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
expr_stmt|;
name|orderQueue
operator|=
name|session
operator|.
name|createQueue
argument_list|(
name|QUEUE
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|orderQueue
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|()
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|message
operator|.
name|getJMSReplyTo
argument_list|()
argument_list|)
decl_stmt|;
name|MapMessage
name|orderMessage
decl_stmt|;
if|if
condition|(
name|message
operator|instanceof
name|MapMessage
condition|)
block|{
name|orderMessage
operator|=
operator|(
name|MapMessage
operator|)
name|message
expr_stmt|;
block|}
else|else
block|{
comment|// End of Stream
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createMessage
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
break|break;
block|}
name|int
name|quantity
init|=
name|orderMessage
operator|.
name|getInt
argument_list|(
literal|"Quantity"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ITEM
operator|+
literal|" Supplier: Vendor ordered "
operator|+
name|quantity
operator|+
literal|" "
operator|+
name|orderMessage
operator|.
name|getString
argument_list|(
literal|"Item"
argument_list|)
argument_list|)
expr_stmt|;
name|MapMessage
name|outMessage
init|=
name|session
operator|.
name|createMapMessage
argument_list|()
decl_stmt|;
name|outMessage
operator|.
name|setInt
argument_list|(
literal|"VendorOrderNumber"
argument_list|,
name|orderMessage
operator|.
name|getInt
argument_list|(
literal|"VendorOrderNumber"
argument_list|)
argument_list|)
expr_stmt|;
name|outMessage
operator|.
name|setString
argument_list|(
literal|"Item"
argument_list|,
name|ITEM
argument_list|)
expr_stmt|;
name|quantity
operator|=
name|Math
operator|.
name|min
argument_list|(
name|orderMessage
operator|.
name|getInt
argument_list|(
literal|"Quantity"
argument_list|)
argument_list|,
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|orderMessage
operator|.
name|getInt
argument_list|(
literal|"Quantity"
argument_list|)
operator|*
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|outMessage
operator|.
name|setInt
argument_list|(
literal|"Quantity"
argument_list|,
name|quantity
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|outMessage
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ITEM
operator|+
literal|" Supplier: Sent "
operator|+
name|quantity
operator|+
literal|" "
operator|+
name|ITEM
operator|+
literal|"(s)"
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ITEM
operator|+
literal|" Supplier: committed transaction"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
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
block|{
name|String
name|url
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
name|String
name|user
init|=
literal|null
decl_stmt|;
name|String
name|password
init|=
literal|null
decl_stmt|;
name|String
name|item
init|=
literal|"HardDrive"
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|1
condition|)
block|{
name|item
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
block|}
name|String
name|queue
decl_stmt|;
if|if
condition|(
literal|"HardDrive"
operator|.
name|equals
argument_list|(
name|item
argument_list|)
condition|)
block|{
name|queue
operator|=
literal|"StorageOrderQueue"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"Monitor"
operator|.
name|equals
argument_list|(
name|item
argument_list|)
condition|)
block|{
name|queue
operator|=
literal|"MonitorOrderQueue"
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Item must be either HardDrive or Monitor"
argument_list|)
throw|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|2
condition|)
block|{
name|url
operator|=
name|args
index|[
literal|1
index|]
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|3
condition|)
block|{
name|user
operator|=
name|args
index|[
literal|2
index|]
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|4
condition|)
block|{
name|password
operator|=
name|args
index|[
literal|3
index|]
expr_stmt|;
block|}
name|Supplier
name|s
init|=
operator|new
name|Supplier
argument_list|(
name|item
argument_list|,
name|queue
argument_list|,
name|url
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
decl_stmt|;
operator|new
name|Thread
argument_list|(
name|s
argument_list|,
literal|"Supplier "
operator|+
name|item
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

