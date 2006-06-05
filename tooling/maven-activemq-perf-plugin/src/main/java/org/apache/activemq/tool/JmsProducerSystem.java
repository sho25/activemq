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
name|util
operator|.
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|JmsProducerSystem
extends|extends
name|JmsClientSystemSupport
block|{
specifier|public
name|void
name|runJmsClient
parameter_list|(
name|String
name|clientName
parameter_list|,
name|Properties
name|clientSettings
parameter_list|)
block|{
name|PerfMeasurementTool
name|sampler
init|=
name|getPerformanceSampler
argument_list|()
decl_stmt|;
name|JmsProducerClient
name|producer
init|=
operator|new
name|JmsProducerClient
argument_list|()
decl_stmt|;
name|producer
operator|.
name|setSettings
argument_list|(
name|clientSettings
argument_list|)
expr_stmt|;
if|if
condition|(
name|sampler
operator|!=
literal|null
condition|)
block|{
name|sampler
operator|.
name|registerClient
argument_list|(
name|producer
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setPerfEventListener
argument_list|(
name|sampler
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|producer
operator|.
name|createJmsTextMessage
argument_list|()
expr_stmt|;
name|producer
operator|.
name|sendMessages
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
name|String
name|getClientName
parameter_list|()
block|{
return|return
literal|"JMS Producer: "
return|;
block|}
specifier|public
name|String
name|getThreadName
parameter_list|()
block|{
return|return
literal|"JMS Producer Thread: "
return|;
block|}
specifier|public
name|String
name|getThreadGroupName
parameter_list|()
block|{
return|return
literal|"JMS Producer Thread Group"
return|;
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
comment|/*String[] options = new String[19];         options[0] = "-Dsampler.duration=60000";     // 1 min         options[1] = "-Dsampler.interval=5000";      // 5 secs         options[2] = "-Dsampler.rampUpTime=10000";   // 10 secs         options[3] = "-Dsampler.rampDownTime=10000"; // 10 secs          options[4] = "-Dclient.spiClass=org.apache.activemq.tool.spi.ActiveMQPojoSPI";         options[5] = "-Dclient.sessTransacted=false";         options[6] = "-Dclient.sessAckMode=autoAck";         options[7] = "-Dclient.destName=topic://FOO.BAR.TEST";         options[8] = "-Dclient.destCount=1";         options[9] = "-Dclient.destComposite=false";          options[10] = "-Dproducer.messageSize=1024";         options[11] = "-Dproducer.sendCount=1000";     // 1000 messages         options[12] = "-Dproducer.sendDuration=60000"; // 1 min         options[13] = "-Dproducer.sendType=time";          options[14] = "-Dfactory.brokerUrl=tcp://localhost:61616";         options[15] = "-Dfactory.asyncSend=true";          options[16] = "-DsysTest.numClients=5";         options[17] = "-DsysTest.totalDests=5";         options[18] = "-DsysTest.destDistro=all";          args = options;*/
name|Properties
name|sysSettings
init|=
operator|new
name|Properties
argument_list|()
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// Get property define options only
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"-D"
argument_list|)
condition|)
block|{
name|String
name|propDefine
init|=
name|args
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|"-D"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|index
init|=
name|propDefine
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|propDefine
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|propDefine
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
decl_stmt|;
name|sysSettings
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
name|JmsProducerSystem
name|sysTest
init|=
operator|new
name|JmsProducerSystem
argument_list|()
decl_stmt|;
name|sysTest
operator|.
name|setReportDirectory
argument_list|(
literal|"./target/Test-perf"
argument_list|)
expr_stmt|;
name|sysTest
operator|.
name|setSettings
argument_list|(
name|sysSettings
argument_list|)
expr_stmt|;
name|sysTest
operator|.
name|runSystemTest
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

