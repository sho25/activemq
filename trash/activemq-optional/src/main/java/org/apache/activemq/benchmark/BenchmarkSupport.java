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
name|benchmark
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IdGenerator
import|;
end_import

begin_comment
comment|/**  * Abstract base class for some simple benchmark tools  */
end_comment

begin_class
specifier|public
class|class
name|BenchmarkSupport
block|{
specifier|protected
name|int
name|connectionCount
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
name|batch
init|=
literal|1000
decl_stmt|;
specifier|protected
name|Destination
name|destination
decl_stmt|;
specifier|protected
name|String
index|[]
name|subjects
decl_stmt|;
specifier|private
name|boolean
name|topic
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|durable
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
specifier|private
name|String
name|url
decl_stmt|;
specifier|private
name|int
name|counter
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Object
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|NumberFormat
name|formatter
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|private
name|AtomicInteger
name|connectionCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|IdGenerator
name|idGenerator
init|=
operator|new
name|IdGenerator
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|timerLoop
decl_stmt|;
specifier|public
name|BenchmarkSupport
parameter_list|()
block|{     }
specifier|public
name|void
name|start
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using: "
operator|+
name|connectionCount
operator|+
literal|" connection(s)"
argument_list|)
expr_stmt|;
name|subjects
operator|=
operator|new
name|String
index|[
name|connectionCount
index|]
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
name|connectionCount
condition|;
name|i
operator|++
control|)
block|{
name|subjects
index|[
name|i
index|]
operator|=
literal|"BENCHMARK.FEED"
operator|+
name|i
expr_stmt|;
block|}
if|if
condition|(
name|useTimerLoop
argument_list|()
condition|)
block|{
name|Thread
name|timer
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|timerLoop
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|timer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|url
return|;
block|}
specifier|public
name|void
name|setUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTopic
parameter_list|()
block|{
return|return
name|topic
return|;
block|}
specifier|public
name|void
name|setTopic
parameter_list|(
name|boolean
name|topic
parameter_list|)
block|{
name|this
operator|.
name|topic
operator|=
name|topic
expr_stmt|;
block|}
specifier|public
name|ActiveMQConnectionFactory
name|getFactory
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
specifier|public
name|void
name|setFactory
parameter_list|(
name|ActiveMQConnectionFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
specifier|public
name|void
name|setSubject
parameter_list|(
name|String
name|subject
parameter_list|)
block|{
name|connectionCount
operator|=
literal|1
expr_stmt|;
name|subjects
operator|=
operator|new
name|String
index|[]
block|{
name|subject
block|}
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDurable
parameter_list|()
block|{
return|return
name|durable
return|;
block|}
specifier|public
name|void
name|setDurable
parameter_list|(
name|boolean
name|durable
parameter_list|)
block|{
name|this
operator|.
name|durable
operator|=
name|durable
expr_stmt|;
block|}
specifier|public
name|int
name|getConnectionCount
parameter_list|()
block|{
return|return
name|connectionCount
return|;
block|}
specifier|public
name|void
name|setConnectionCount
parameter_list|(
name|int
name|connectionCount
parameter_list|)
block|{
name|this
operator|.
name|connectionCount
operator|=
name|connectionCount
expr_stmt|;
block|}
specifier|protected
name|Session
name|createSession
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|factory
operator|==
literal|null
condition|)
block|{
name|factory
operator|=
name|createFactory
argument_list|()
expr_stmt|;
block|}
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|int
name|value
init|=
name|connectionCounter
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Created connection: "
operator|+
name|value
operator|+
literal|" = "
operator|+
name|connection
argument_list|)
expr_stmt|;
if|if
condition|(
name|durable
condition|)
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|idGenerator
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|addResource
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|addResource
argument_list|(
name|session
argument_list|)
expr_stmt|;
return|return
name|session
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createFactory
parameter_list|()
block|{
name|ActiveMQConnectionFactory
name|answer
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|getUrl
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
specifier|synchronized
name|void
name|count
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|counter
operator|+=
name|count
expr_stmt|;
comment|/*          * if (counter> batch) { counter = 0; long current =          * System.currentTimeMillis(); double end = current - time; end /= 1000;          * time = current; System.out.println("Processed " + batch + " messages          * in " + end + " (secs)"); }          */
block|}
specifier|protected
specifier|synchronized
name|int
name|resetCount
parameter_list|()
block|{
name|int
name|answer
init|=
name|counter
decl_stmt|;
name|counter
operator|=
literal|0
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|timerLoop
parameter_list|()
block|{
name|int
name|times
init|=
literal|0
decl_stmt|;
name|int
name|total
init|=
literal|0
decl_stmt|;
name|int
name|dumpVmStatsFrequency
init|=
literal|10
decl_stmt|;
name|Runtime
name|runtime
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|int
name|processed
init|=
name|resetCount
argument_list|()
decl_stmt|;
name|double
name|average
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|processed
operator|>
literal|0
condition|)
block|{
name|total
operator|+=
name|processed
expr_stmt|;
name|times
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|times
operator|>
literal|0
condition|)
block|{
name|average
operator|=
name|total
operator|/
operator|(
name|double
operator|)
name|times
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" Processed: "
operator|+
name|processed
operator|+
literal|" messages this second. Average: "
operator|+
name|average
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|times
operator|%
name|dumpVmStatsFrequency
operator|)
operator|==
literal|0
operator|&&
name|times
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Used memory: "
operator|+
name|asMemoryString
argument_list|(
name|runtime
operator|.
name|totalMemory
argument_list|()
operator|-
name|runtime
operator|.
name|freeMemory
argument_list|()
argument_list|)
operator|+
literal|" Free memory: "
operator|+
name|asMemoryString
argument_list|(
name|runtime
operator|.
name|freeMemory
argument_list|()
argument_list|)
operator|+
literal|" Total memory: "
operator|+
name|asMemoryString
argument_list|(
name|runtime
operator|.
name|totalMemory
argument_list|()
argument_list|)
operator|+
literal|" Max memory: "
operator|+
name|asMemoryString
argument_list|(
name|runtime
operator|.
name|maxMemory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|String
name|asMemoryString
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|formatter
operator|.
name|format
argument_list|(
name|value
operator|/
literal|1024
argument_list|)
operator|+
literal|" K"
return|;
block|}
specifier|protected
name|boolean
name|useTimerLoop
parameter_list|()
block|{
return|return
name|timerLoop
return|;
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|subject
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|topic
condition|)
block|{
return|return
name|session
operator|.
name|createTopic
argument_list|(
name|subject
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|session
operator|.
name|createQueue
argument_list|(
name|subject
argument_list|)
return|;
block|}
block|}
specifier|protected
name|void
name|addResource
parameter_list|(
name|Object
name|resource
parameter_list|)
block|{
name|resources
operator|.
name|add
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getCounter
parameter_list|()
block|{
return|return
name|counter
return|;
block|}
specifier|public
name|void
name|setTimerLoop
parameter_list|(
name|boolean
name|timerLoop
parameter_list|)
block|{
name|this
operator|.
name|timerLoop
operator|=
name|timerLoop
expr_stmt|;
block|}
specifier|protected
specifier|static
name|boolean
name|parseBoolean
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
name|text
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

