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
name|store
operator|.
name|kahadaptor
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
name|store
operator|.
name|ReferenceStore
operator|.
name|ReferenceData
import|;
end_import

begin_class
specifier|public
class|class
name|ReferenceRecord
block|{
specifier|private
name|String
name|messageId
decl_stmt|;
specifier|private
name|ReferenceData
name|data
decl_stmt|;
specifier|public
name|ReferenceRecord
parameter_list|()
block|{     }
specifier|public
name|ReferenceRecord
parameter_list|(
name|String
name|messageId
parameter_list|,
name|ReferenceData
name|data
parameter_list|)
block|{
name|this
operator|.
name|messageId
operator|=
name|messageId
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
comment|/**      * @return the data      */
specifier|public
name|ReferenceData
name|getData
parameter_list|()
block|{
return|return
name|this
operator|.
name|data
return|;
block|}
comment|/**      * @param data the data to set      */
specifier|public
name|void
name|setData
parameter_list|(
name|ReferenceData
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
comment|/**      * @return the messageId      */
specifier|public
name|String
name|getMessageId
parameter_list|()
block|{
return|return
name|this
operator|.
name|messageId
return|;
block|}
comment|/**      * @param messageId the messageId to set      */
specifier|public
name|void
name|setMessageId
parameter_list|(
name|String
name|messageId
parameter_list|)
block|{
name|this
operator|.
name|messageId
operator|=
name|messageId
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ReferenceRecord(id="
operator|+
name|messageId
operator|+
literal|",data="
operator|+
name|data
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit
