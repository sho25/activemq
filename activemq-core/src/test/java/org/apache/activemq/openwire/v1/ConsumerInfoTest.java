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
name|openwire
operator|.
name|v1
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
comment|/**  * Test case for the OpenWire marshalling for ConsumerInfo  *  *  * NOTE!: This file is auto generated - do not modify!  *        if you need to make a change, please see the modify the groovy scripts in the  *        under src/gram/script and then use maven openwire:generate to regenerate   *        this file.  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ConsumerInfoTest
extends|extends
name|BaseCommandTestSupport
block|{
specifier|public
specifier|static
name|ConsumerInfoTest
name|SINGLETON
init|=
operator|new
name|ConsumerInfoTest
argument_list|()
decl_stmt|;
specifier|public
name|Object
name|createObject
parameter_list|()
throws|throws
name|Exception
block|{
name|ConsumerInfo
name|info
init|=
operator|new
name|ConsumerInfo
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
name|ConsumerInfo
name|info
init|=
operator|(
name|ConsumerInfo
operator|)
name|object
decl_stmt|;
name|info
operator|.
name|setConsumerId
argument_list|(
name|createConsumerId
argument_list|(
literal|"ConsumerId:1"
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setBrowser
argument_list|(
literal|true
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
name|info
operator|.
name|setPrefetchSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|info
operator|.
name|setMaximumPendingMessageLimit
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDispatchAsync
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSelector
argument_list|(
literal|"Selector:3"
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubcriptionName
argument_list|(
literal|"SubcriptionName:4"
argument_list|)
expr_stmt|;
name|info
operator|.
name|setNoLocal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|info
operator|.
name|setExclusive
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|info
operator|.
name|setRetroactive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|info
operator|.
name|setPriority
argument_list|(
operator|(
name|byte
operator|)
literal|1
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
literal|"BrokerPath:5"
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
name|setNetworkSubscription
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

