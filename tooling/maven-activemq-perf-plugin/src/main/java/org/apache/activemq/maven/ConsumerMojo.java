begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|maven
package|;
end_package

begin_comment
comment|/*  * Copyright 2001-2005 The Apache Software Foundation.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|JmsConsumerSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|AbstractMojo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|MojoExecutionException
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

begin_comment
comment|/**  * Goal which touches a timestamp file.  *  * @goal consumer  * @phase process-sources  */
end_comment

begin_class
specifier|public
class|class
name|ConsumerMojo
extends|extends
name|AbstractMojo
block|{
comment|/**      * @parameter expression="${sampler.duration}" default-value="60000"      * @required      */
specifier|private
name|String
name|duration
decl_stmt|;
comment|/**      * @parameter expression="${sampler.interval}" default-value="5000"      * @required      */
specifier|private
name|String
name|interval
decl_stmt|;
comment|/**      * @parameter expression="${sampler.rampUpTime}" default-value="10000"      * @required      */
specifier|private
name|String
name|rampUpTime
decl_stmt|;
comment|/**      * @parameter expression="${sampler.rampDownTime}" default-value="10000"      * @required      */
specifier|private
name|String
name|rampDownTime
decl_stmt|;
comment|/**      * @parameter expression="${client.spiClass}" default-value="org.apache.activemq.tool.spi.ActiveMQPojoSPI"      * @required      */
specifier|private
name|String
name|spiClass
decl_stmt|;
comment|/**      * @parameter expression="${client.sessTransacted}" default-value="false"      * @required      */
specifier|private
name|String
name|sessTransacted
decl_stmt|;
comment|/**      * @parameter expression="${client.sessAckMode}" default-value="autoAck"      * @required      */
specifier|private
name|String
name|sessAckMode
decl_stmt|;
comment|/**      * @parameter expression="${client.destName}" default-value="topic://FOO.BAR.TEST"      * @required      */
specifier|private
name|String
name|destName
decl_stmt|;
comment|/**      * @parameter expression="${client.destCount}" default-value="1"      * @required      */
specifier|private
name|String
name|destCount
decl_stmt|;
comment|/**      * @parameter expression="${client.destComposite}" default-value="false"      * @required      */
specifier|private
name|String
name|destComposite
decl_stmt|;
comment|/**      * @parameter expression="${consumer.durable}" default-value="false"      * @required      */
specifier|private
name|String
name|durable
decl_stmt|;
comment|/**      * @parameter expression="${consumer.asyncRecv}" default-value="true"      * @required      */
specifier|private
name|String
name|asyncRecv
decl_stmt|;
comment|/**      * @parameter expression="${consumer.recvCount}" default-value="1000"      * @required      */
specifier|private
name|String
name|recvCount
decl_stmt|;
comment|/**      * @parameter expression="${consumer.recvDuration}" default-value="60000"      * @required      */
specifier|private
name|String
name|recvDuration
decl_stmt|;
comment|/**      * @parameter expression="${consumer.recvType}" default-value="time"      * @required      */
specifier|private
name|String
name|recvType
decl_stmt|;
comment|/**      * @parameter expression="${factory.brokerUrl}" default-value="tcp://localhost:61616"      * @required      */
specifier|private
name|String
name|brokerUrl
decl_stmt|;
comment|/**      * @parameter expression="${factory.optimAck}" default-value="true"      * @required      */
specifier|private
name|String
name|optimAck
decl_stmt|;
comment|/**      * @parameter expression="${factory.optimDispatch}" default-value="true"      * @required      */
specifier|private
name|String
name|optimDispatch
decl_stmt|;
comment|/**      * @parameter expression="${factory.prefetchQueue}" default-value="10"      * @required      */
specifier|private
name|String
name|prefetchQueue
decl_stmt|;
comment|/**      * @parameter expression="${factory.prefetchTopic}" default-value="10"      * @required      */
specifier|private
name|String
name|prefetchTopic
decl_stmt|;
comment|/**      * @parameter expression="${factory.useRetroactive}" default-value="false"      * @required      */
specifier|private
name|String
name|useRetroactive
decl_stmt|;
comment|/**      * @parameter expression="${sysTest.numClients}" default-value="5"      * @required      */
specifier|private
name|String
name|numClients
decl_stmt|;
comment|/**      * @parameter expression="${sysTest.totalDests}" default-value="5"      * @required      */
specifier|private
name|String
name|totalDests
decl_stmt|;
comment|/**      * @parameter expression="${sysTest.destDistro}" default-value="all"      * @required      */
specifier|private
name|String
name|destDistro
decl_stmt|;
comment|/**      * @parameter expression="${sysTest.reportDirectory}" default-value="${project.build.directory}/test-perf"      * @required      */
specifier|private
name|String
name|reportDirectory
decl_stmt|;
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
try|try
block|{
name|JmsConsumerSystem
operator|.
name|main
argument_list|(
name|createArgument
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|String
index|[]
name|createArgument
parameter_list|()
block|{
name|String
index|[]
name|options
init|=
operator|new
name|String
index|[
literal|25
index|]
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--- "
operator|+
name|duration
operator|+
literal|" ----"
argument_list|)
expr_stmt|;
name|options
index|[
literal|0
index|]
operator|=
literal|"-Dsampler.duration="
operator|+
name|duration
expr_stmt|;
comment|// 1 min
name|options
index|[
literal|1
index|]
operator|=
literal|"-Dsampler.interval="
operator|+
name|interval
expr_stmt|;
comment|// 5 secs
name|options
index|[
literal|2
index|]
operator|=
literal|"-Dsampler.rampUpTime="
operator|+
name|rampUpTime
expr_stmt|;
comment|// 10 secs
name|options
index|[
literal|3
index|]
operator|=
literal|"-Dsampler.rampDownTime="
operator|+
name|rampDownTime
expr_stmt|;
comment|// 10 secs
name|options
index|[
literal|4
index|]
operator|=
literal|"-Dclient.spiClass="
operator|+
name|spiClass
expr_stmt|;
name|options
index|[
literal|5
index|]
operator|=
literal|"-Dclient.sessTransacted="
operator|+
name|sessTransacted
expr_stmt|;
name|options
index|[
literal|6
index|]
operator|=
literal|"-Dclient.sessAckMode="
operator|+
name|sessAckMode
expr_stmt|;
name|options
index|[
literal|7
index|]
operator|=
literal|"-Dclient.destName="
operator|+
name|destName
expr_stmt|;
name|options
index|[
literal|8
index|]
operator|=
literal|"-Dclient.destCount="
operator|+
name|destCount
expr_stmt|;
name|options
index|[
literal|9
index|]
operator|=
literal|"-Dclient.destComposite="
operator|+
name|destComposite
expr_stmt|;
name|options
index|[
literal|10
index|]
operator|=
literal|"-Dconsumer.durable="
operator|+
name|durable
expr_stmt|;
name|options
index|[
literal|11
index|]
operator|=
literal|"-Dconsumer.asyncRecv="
operator|+
name|asyncRecv
expr_stmt|;
name|options
index|[
literal|12
index|]
operator|=
literal|"-Dconsumer.recvCount="
operator|+
name|recvCount
expr_stmt|;
comment|// 1000 messages
name|options
index|[
literal|13
index|]
operator|=
literal|"-Dconsumer.recvDuration="
operator|+
name|recvDuration
expr_stmt|;
comment|// 1 min
name|options
index|[
literal|14
index|]
operator|=
literal|"-Dconsumer.recvType="
operator|+
name|recvType
expr_stmt|;
name|options
index|[
literal|15
index|]
operator|=
literal|"-Dfactory.brokerUrl="
operator|+
name|brokerUrl
expr_stmt|;
name|options
index|[
literal|16
index|]
operator|=
literal|"-Dfactory.optimAck="
operator|+
name|optimAck
expr_stmt|;
name|options
index|[
literal|17
index|]
operator|=
literal|"-Dfactory.optimDispatch="
operator|+
name|optimDispatch
expr_stmt|;
name|options
index|[
literal|18
index|]
operator|=
literal|"-Dfactory.prefetchQueue="
operator|+
name|prefetchQueue
expr_stmt|;
name|options
index|[
literal|19
index|]
operator|=
literal|"-Dfactory.prefetchTopic="
operator|+
name|prefetchTopic
expr_stmt|;
name|options
index|[
literal|20
index|]
operator|=
literal|"-Dfactory.useRetroactive="
operator|+
name|useRetroactive
expr_stmt|;
name|options
index|[
literal|21
index|]
operator|=
literal|"-DsysTest.numClients="
operator|+
name|numClients
expr_stmt|;
name|options
index|[
literal|22
index|]
operator|=
literal|"-DsysTest.totalDests="
operator|+
name|totalDests
expr_stmt|;
name|options
index|[
literal|23
index|]
operator|=
literal|"-DsysTest.destDistro="
operator|+
name|destDistro
expr_stmt|;
name|options
index|[
literal|24
index|]
operator|=
literal|"-DsysTest.reportDirectory="
operator|+
name|reportDirectory
expr_stmt|;
return|return
name|options
return|;
block|}
block|}
end_class

end_unit

