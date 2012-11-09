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
name|cursors
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
name|region
operator|.
name|MessageReference
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
name|util
operator|.
name|LinkedNode
import|;
end_import

begin_class
specifier|public
class|class
name|PendingNode
extends|extends
name|LinkedNode
block|{
specifier|private
specifier|final
name|MessageReference
name|message
decl_stmt|;
specifier|private
specifier|final
name|OrderedPendingList
name|list
decl_stmt|;
specifier|public
name|PendingNode
parameter_list|(
name|OrderedPendingList
name|list
parameter_list|,
name|MessageReference
name|message
parameter_list|)
block|{
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
name|MessageReference
name|getMessage
parameter_list|()
block|{
return|return
name|this
operator|.
name|message
return|;
block|}
name|OrderedPendingList
name|getList
parameter_list|()
block|{
return|return
name|this
operator|.
name|list
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|PendingNode
name|n
init|=
operator|(
name|PendingNode
operator|)
name|getNext
argument_list|()
decl_stmt|;
name|String
name|str
init|=
literal|"PendingNode("
decl_stmt|;
name|str
operator|+=
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
operator|+
literal|"),root="
operator|+
name|isHeadNode
argument_list|()
operator|+
literal|",next="
operator|+
operator|(
name|n
operator|!=
literal|null
condition|?
name|System
operator|.
name|identityHashCode
argument_list|(
name|n
argument_list|)
else|:
literal|"NULL"
operator|)
expr_stmt|;
return|return
name|str
return|;
block|}
block|}
end_class

end_unit
