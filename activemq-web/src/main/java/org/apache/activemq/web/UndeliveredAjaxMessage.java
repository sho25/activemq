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
name|web
package|;
end_package

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

begin_class
class|class
name|UndeliveredAjaxMessage
block|{
specifier|private
name|Message
name|message
decl_stmt|;
specifier|private
name|MessageConsumer
name|consumer
decl_stmt|;
name|UndeliveredAjaxMessage
parameter_list|(
name|Message
name|message
parameter_list|,
name|MessageConsumer
name|consumer
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
block|}
specifier|public
name|MessageConsumer
name|getConsumer
parameter_list|()
block|{
return|return
name|this
operator|.
name|consumer
return|;
block|}
specifier|public
name|Message
name|getMessage
parameter_list|()
block|{
return|return
name|this
operator|.
name|message
return|;
block|}
block|}
end_class

end_unit

