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
name|protocol
package|;
end_package

begin_comment
comment|/**  *  *  */
end_comment

begin_class
specifier|public
class|class
name|MqttProtocolVerifier
implements|implements
name|ProtocolVerifier
block|{
comment|/* (non-Javadoc)      * @see org.apache.activemq.broker.transport.protocol.ProtocolVerifier#isProtocol(byte[])      */
annotation|@
name|Override
specifier|public
name|boolean
name|isProtocol
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
name|boolean
name|mqtt311
init|=
name|value
index|[
literal|4
index|]
operator|==
literal|77
operator|&&
comment|// M
name|value
index|[
literal|5
index|]
operator|==
literal|81
operator|&&
comment|// Q
name|value
index|[
literal|6
index|]
operator|==
literal|84
operator|&&
comment|// T
name|value
index|[
literal|7
index|]
operator|==
literal|84
decl_stmt|;
comment|// T
name|boolean
name|mqtt31
init|=
name|value
index|[
literal|4
index|]
operator|==
literal|77
operator|&&
comment|// M
name|value
index|[
literal|5
index|]
operator|==
literal|81
operator|&&
comment|// Q
name|value
index|[
literal|6
index|]
operator|==
literal|73
operator|&&
comment|// I
name|value
index|[
literal|7
index|]
operator|==
literal|115
decl_stmt|;
comment|// s
return|return
name|mqtt311
operator|||
name|mqtt31
return|;
block|}
block|}
end_class

end_unit

