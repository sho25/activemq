begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|openwire
operator|.
name|v2
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|openwire
operator|.
name|*
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
name|command
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test case for the OpenWire marshalling for ProducerInfo  *  *  * NOTE!: This file is auto generated - do not modify!  *        if you need to make a change, please see the modify the groovy scripts in the  *        under src/gram/script and then use maven openwire:generate to regenerate   *        this file.  *  * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|ProducerInfoTest
extends|extends
name|BaseCommandTestSupport
block|{
specifier|public
specifier|static
name|ProducerInfoTest
name|SINGLETON
init|=
operator|new
name|ProducerInfoTest
argument_list|()
decl_stmt|;
specifier|public
name|Object
name|createObject
parameter_list|()
throws|throws
name|Exception
block|{
name|ProducerInfo
name|info
init|=
operator|new
name|ProducerInfo
argument_list|()
decl_stmt|;
name|populateObject
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|void
name|populateObject
parameter_list|(
name|Object
name|object
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|populateObject
argument_list|(
name|object
argument_list|)
expr_stmt|;
name|ProducerInfo
name|info
init|=
operator|(
name|ProducerInfo
operator|)
name|object
decl_stmt|;
name|info
operator|.
name|setProducerId
argument_list|(
name|createProducerId
argument_list|(
literal|"ProducerId:1"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
name|createActiveMQDestination
argument_list|(
literal|"Destination:2"
argument_list|)
argument_list|)
expr_stmt|;
block|{
name|BrokerId
name|value
index|[]
init|=
operator|new
name|BrokerId
index|[
literal|2
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|value
index|[
name|i
index|]
operator|=
name|createBrokerId
argument_list|(
literal|"BrokerPath:3"
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setBrokerPath
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setDispatchAsync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

