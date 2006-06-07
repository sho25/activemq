begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
package|;
end_package

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|Iterator
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
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|PerfMeasurementTool
implements|implements
name|PerfEventListener
implements|,
name|Runnable
block|{
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX_CONFIG_SYSTEM_TEST
init|=
literal|"sampler."
decl_stmt|;
specifier|private
name|long
name|duration
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 5 mins by default test duration
specifier|private
name|long
name|interval
init|=
literal|1000
decl_stmt|;
comment|// 1 sec sample interval
specifier|private
name|long
name|rampUpTime
init|=
literal|1
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 1 min default test ramp up time
specifier|private
name|long
name|rampDownTime
init|=
literal|1
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|// 1 min default test ramp down time
specifier|private
name|long
name|sampleIndex
init|=
literal|0
decl_stmt|;
specifier|private
name|AtomicBoolean
name|start
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|AtomicBoolean
name|isRunning
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|PrintWriter
name|writer
init|=
literal|null
decl_stmt|;
specifier|private
name|Properties
name|samplerSettings
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|private
name|List
name|perfClients
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|public
name|void
name|registerClient
parameter_list|(
name|PerfMeasurable
name|client
parameter_list|)
block|{
name|client
operator|.
name|setPerfEventListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|perfClients
operator|.
name|add
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|registerClient
parameter_list|(
name|PerfMeasurable
index|[]
name|clients
parameter_list|)
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
name|clients
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|registerClient
argument_list|(
name|clients
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Properties
name|getSamplerSettings
parameter_list|()
block|{
return|return
name|samplerSettings
return|;
block|}
specifier|public
name|void
name|setSamplerSettings
parameter_list|(
name|Properties
name|samplerSettings
parameter_list|)
block|{
name|this
operator|.
name|samplerSettings
operator|=
name|samplerSettings
expr_stmt|;
name|ReflectionUtil
operator|.
name|configureClass
argument_list|(
name|this
argument_list|,
name|samplerSettings
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PrintWriter
name|getWriter
parameter_list|()
block|{
return|return
name|writer
return|;
block|}
specifier|public
name|void
name|setWriter
parameter_list|(
name|PrintWriter
name|writer
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
specifier|public
name|long
name|getDuration
parameter_list|()
block|{
return|return
name|duration
return|;
block|}
specifier|public
name|void
name|setDuration
parameter_list|(
name|long
name|duration
parameter_list|)
block|{
name|this
operator|.
name|duration
operator|=
name|duration
expr_stmt|;
block|}
specifier|public
name|long
name|getInterval
parameter_list|()
block|{
return|return
name|interval
return|;
block|}
specifier|public
name|void
name|setInterval
parameter_list|(
name|long
name|interval
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
block|}
specifier|public
name|long
name|getRampUpTime
parameter_list|()
block|{
return|return
name|rampUpTime
return|;
block|}
specifier|public
name|void
name|setRampUpTime
parameter_list|(
name|long
name|rampUpTime
parameter_list|)
block|{
name|this
operator|.
name|rampUpTime
operator|=
name|rampUpTime
expr_stmt|;
block|}
specifier|public
name|long
name|getRampDownTime
parameter_list|()
block|{
return|return
name|rampDownTime
return|;
block|}
specifier|public
name|void
name|setRampDownTime
parameter_list|(
name|long
name|rampDownTime
parameter_list|)
block|{
name|this
operator|.
name|rampDownTime
operator|=
name|rampDownTime
expr_stmt|;
block|}
specifier|public
name|void
name|onConfigStart
parameter_list|(
name|PerfMeasurable
name|client
parameter_list|)
block|{     }
specifier|public
name|void
name|onConfigEnd
parameter_list|(
name|PerfMeasurable
name|client
parameter_list|)
block|{     }
specifier|public
name|void
name|onPublishStart
parameter_list|(
name|PerfMeasurable
name|client
parameter_list|)
block|{     }
specifier|public
name|void
name|onPublishEnd
parameter_list|(
name|PerfMeasurable
name|client
parameter_list|)
block|{     }
specifier|public
name|void
name|onConsumeStart
parameter_list|(
name|PerfMeasurable
name|client
parameter_list|)
block|{     }
specifier|public
name|void
name|onConsumeEnd
parameter_list|(
name|PerfMeasurable
name|client
parameter_list|)
block|{     }
specifier|public
name|void
name|onJMSException
parameter_list|(
name|PerfMeasurable
name|client
parameter_list|,
name|JMSException
name|e
parameter_list|)
block|{     }
specifier|public
name|void
name|onException
parameter_list|(
name|PerfMeasurable
name|client
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startSampler
parameter_list|()
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|t
operator|.
name|setName
argument_list|(
literal|"Performance Sampler"
argument_list|)
expr_stmt|;
name|isRunning
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// Compute for the actual duration window of the sampler
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|duration
operator|-
name|rampDownTime
decl_stmt|;
try|try
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|rampUpTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{             }
comment|// Let's reset the throughput first and start getting the samples
for|for
control|(
name|Iterator
name|i
init|=
name|perfClients
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|PerfMeasurable
name|client
init|=
operator|(
name|PerfMeasurable
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|client
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|endTime
operator|&&
operator|!
name|stop
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{                 }
name|sampleClients
argument_list|()
expr_stmt|;
name|sampleIndex
operator|++
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|isRunning
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|isRunning
init|)
block|{
name|isRunning
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|sampleClients
parameter_list|()
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|perfClients
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|PerfMeasurable
name|client
init|=
operator|(
name|PerfMeasurable
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|getWriter
argument_list|()
operator|.
name|println
argument_list|(
literal|"<sample index="
operator|+
name|sampleIndex
operator|+
literal|" name="
operator|+
name|client
operator|.
name|getClientName
argument_list|()
operator|+
literal|" throughput="
operator|+
name|client
operator|.
name|getThroughput
argument_list|()
operator|+
literal|"/>"
argument_list|)
expr_stmt|;
name|client
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|waitForSamplerToFinish
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
while|while
condition|(
name|isRunning
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
synchronized|synchronized
init|(
name|isRunning
init|)
block|{
name|isRunning
operator|.
name|wait
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{             }
block|}
block|}
block|}
end_class

end_unit

