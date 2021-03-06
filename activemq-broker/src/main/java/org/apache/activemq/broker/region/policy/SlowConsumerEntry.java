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
name|broker
operator|.
name|region
operator|.
name|policy
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|ConnectionContext
import|;
end_import

begin_class
specifier|public
class|class
name|SlowConsumerEntry
block|{
specifier|final
name|ConnectionContext
name|context
decl_stmt|;
name|Object
name|subscription
decl_stmt|;
name|int
name|slowCount
init|=
literal|1
decl_stmt|;
name|int
name|markCount
init|=
literal|0
decl_stmt|;
name|SlowConsumerEntry
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
specifier|public
name|void
name|slow
parameter_list|()
block|{
name|slowCount
operator|++
expr_stmt|;
block|}
specifier|public
name|void
name|mark
parameter_list|()
block|{
name|markCount
operator|++
expr_stmt|;
block|}
specifier|public
name|void
name|setSubscription
parameter_list|(
name|Object
name|subscriptionObjectName
parameter_list|)
block|{
name|this
operator|.
name|subscription
operator|=
name|subscriptionObjectName
expr_stmt|;
block|}
specifier|public
name|Object
name|getSubscription
parameter_list|()
block|{
return|return
name|subscription
return|;
block|}
specifier|public
name|int
name|getSlowCount
parameter_list|()
block|{
return|return
name|slowCount
return|;
block|}
specifier|public
name|int
name|getMarkCount
parameter_list|()
block|{
return|return
name|markCount
return|;
block|}
block|}
end_class

end_unit

