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
name|failover
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|transport
operator|.
name|tcp
operator|.
name|TransportUriTest
import|;
end_import

begin_class
specifier|public
class|class
name|FailoverUriTest
extends|extends
name|TransportUriTest
block|{
annotation|@
name|Override
specifier|public
name|void
name|initCombosForTestUriOptionsWork
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"prefix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"failover:("
block|,
literal|"failover://("
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"postfix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|")?initialReconnectDelay=1000&maxReconnectDelay=1000"
block|,
literal|"?wireFormat.tightEncodingEnabled=false)?jms.useAsyncSend=true&jms.copyMessageOnSend=false"
block|,
literal|"?wireFormat.maxInactivityDuration=0&keepAlive=true)?jms.prefetchPolicy.all=500&initialReconnectDelay=10000&useExponentialBackOff=false&maxReconnectAttempts=0&randomize=false"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|initCombosForTestBadVersionNumberDoesNotWork
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"prefix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"failover:("
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"postfix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|")?initialReconnectDelay=1000&maxReconnectDelay=1000"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|initCombosForTestBadPropertyNameFails
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"prefix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"failover:("
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"postfix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|")?initialReconnectDelay=1000&maxReconnectDelay=1000"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|FailoverUriTest
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

