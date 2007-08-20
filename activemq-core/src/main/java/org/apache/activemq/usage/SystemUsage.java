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
name|usage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|Service
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
name|kaha
operator|.
name|Store
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
name|store
operator|.
name|PersistenceAdapter
import|;
end_import

begin_comment
comment|/**  * Holder for Usage instances for memory, store and temp files Main use case is  * manage memory usage.  *   * @org.apache.xbean.XBean  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|SystemUsage
implements|implements
name|Service
block|{
specifier|private
name|SystemUsage
name|parent
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|MemoryUsage
name|memoryUsage
decl_stmt|;
specifier|private
name|StoreUsage
name|storeUsage
decl_stmt|;
specifier|private
name|TempUsage
name|tempUsage
decl_stmt|;
comment|/**      * True if someone called setSendFailIfNoSpace() on this particular usage      * manager      */
specifier|private
name|boolean
name|sendFailIfNoSpaceExplicitySet
decl_stmt|;
specifier|private
name|boolean
name|sendFailIfNoSpace
decl_stmt|;
specifier|private
name|List
argument_list|<
name|SystemUsage
argument_list|>
name|children
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|SystemUsage
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|SystemUsage
parameter_list|()
block|{
name|this
argument_list|(
literal|"default"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SystemUsage
parameter_list|(
name|String
name|name
parameter_list|,
name|PersistenceAdapter
name|adapter
parameter_list|,
name|Store
name|tempStore
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|memoryUsage
operator|=
operator|new
name|MemoryUsage
argument_list|(
name|name
operator|+
literal|":memory"
argument_list|)
expr_stmt|;
name|this
operator|.
name|storeUsage
operator|=
operator|new
name|StoreUsage
argument_list|(
name|name
operator|+
literal|":store"
argument_list|,
name|adapter
argument_list|)
expr_stmt|;
name|this
operator|.
name|tempUsage
operator|=
operator|new
name|TempUsage
argument_list|(
name|name
operator|+
literal|":temp"
argument_list|,
name|tempStore
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SystemUsage
parameter_list|(
name|SystemUsage
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|memoryUsage
operator|=
operator|new
name|MemoryUsage
argument_list|(
name|parent
operator|.
name|memoryUsage
argument_list|,
name|name
operator|+
literal|":memory"
argument_list|)
expr_stmt|;
name|this
operator|.
name|storeUsage
operator|=
operator|new
name|StoreUsage
argument_list|(
name|parent
operator|.
name|storeUsage
argument_list|,
name|name
operator|+
literal|":store"
argument_list|)
expr_stmt|;
name|this
operator|.
name|tempUsage
operator|=
operator|new
name|TempUsage
argument_list|(
name|parent
operator|.
name|tempUsage
argument_list|,
name|name
operator|+
literal|":temp"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * @return the memoryUsage      */
specifier|public
name|MemoryUsage
name|getMemoryUsage
parameter_list|()
block|{
return|return
name|this
operator|.
name|memoryUsage
return|;
block|}
comment|/**      * @return the storeUsage      */
specifier|public
name|StoreUsage
name|getStoreUsage
parameter_list|()
block|{
return|return
name|this
operator|.
name|storeUsage
return|;
block|}
comment|/**      * @return the tempDiskUsage      */
specifier|public
name|TempUsage
name|getTempUsage
parameter_list|()
block|{
return|return
name|this
operator|.
name|tempUsage
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"UsageManager("
operator|+
name|getName
argument_list|()
operator|+
literal|")"
return|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|addChild
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|memoryUsage
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeUsage
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|tempUsage
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|removeChild
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|memoryUsage
operator|.
name|stop
argument_list|()
expr_stmt|;
name|this
operator|.
name|storeUsage
operator|.
name|stop
argument_list|()
expr_stmt|;
name|this
operator|.
name|tempUsage
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets whether or not a send() should fail if there is no space free. The      * default value is false which means to block the send() method until space      * becomes available      */
specifier|public
name|void
name|setSendFailIfNoSpace
parameter_list|(
name|boolean
name|failProducerIfNoSpace
parameter_list|)
block|{
name|sendFailIfNoSpaceExplicitySet
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|sendFailIfNoSpace
operator|=
name|failProducerIfNoSpace
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSendFailIfNoSpace
parameter_list|()
block|{
if|if
condition|(
name|sendFailIfNoSpaceExplicitySet
operator|||
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|sendFailIfNoSpace
return|;
block|}
else|else
block|{
return|return
name|parent
operator|.
name|isSendFailIfNoSpace
argument_list|()
return|;
block|}
block|}
specifier|private
name|void
name|addChild
parameter_list|(
name|SystemUsage
name|child
parameter_list|)
block|{
name|children
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|removeChild
parameter_list|(
name|SystemUsage
name|child
parameter_list|)
block|{
name|children
operator|.
name|remove
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SystemUsage
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
specifier|public
name|void
name|setParent
parameter_list|(
name|SystemUsage
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSendFailIfNoSpaceExplicitySet
parameter_list|()
block|{
return|return
name|sendFailIfNoSpaceExplicitySet
return|;
block|}
specifier|public
name|void
name|setSendFailIfNoSpaceExplicitySet
parameter_list|(
name|boolean
name|sendFailIfNoSpaceExplicitySet
parameter_list|)
block|{
name|this
operator|.
name|sendFailIfNoSpaceExplicitySet
operator|=
name|sendFailIfNoSpaceExplicitySet
expr_stmt|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|memoryUsage
operator|.
name|setName
argument_list|(
name|name
operator|+
literal|":memory"
argument_list|)
expr_stmt|;
name|this
operator|.
name|storeUsage
operator|.
name|setName
argument_list|(
name|name
operator|+
literal|":store"
argument_list|)
expr_stmt|;
name|this
operator|.
name|tempUsage
operator|.
name|setName
argument_list|(
name|name
operator|+
literal|":temp"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setMemoryUsage
parameter_list|(
name|MemoryUsage
name|memoryUsage
parameter_list|)
block|{
if|if
condition|(
name|memoryUsage
operator|.
name|getName
argument_list|()
operator|==
literal|null
condition|)
block|{
name|memoryUsage
operator|.
name|setName
argument_list|(
name|this
operator|.
name|memoryUsage
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|memoryUsage
operator|.
name|setParent
argument_list|(
name|parent
operator|.
name|memoryUsage
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|memoryUsage
operator|=
name|memoryUsage
expr_stmt|;
block|}
specifier|public
name|void
name|setStoreUsage
parameter_list|(
name|StoreUsage
name|storeUsage
parameter_list|)
block|{
if|if
condition|(
name|storeUsage
operator|.
name|getStore
argument_list|()
operator|==
literal|null
condition|)
block|{
name|storeUsage
operator|.
name|setStore
argument_list|(
name|this
operator|.
name|storeUsage
operator|.
name|getStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|storeUsage
operator|.
name|getName
argument_list|()
operator|==
literal|null
condition|)
block|{
name|storeUsage
operator|.
name|setName
argument_list|(
name|this
operator|.
name|storeUsage
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|storeUsage
operator|.
name|setParent
argument_list|(
name|parent
operator|.
name|storeUsage
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|storeUsage
operator|=
name|storeUsage
expr_stmt|;
block|}
specifier|public
name|void
name|setTempUsage
parameter_list|(
name|TempUsage
name|tempDiskUsage
parameter_list|)
block|{
if|if
condition|(
name|tempDiskUsage
operator|.
name|getStore
argument_list|()
operator|==
literal|null
condition|)
block|{
name|tempDiskUsage
operator|.
name|setStore
argument_list|(
name|this
operator|.
name|tempUsage
operator|.
name|getStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tempDiskUsage
operator|.
name|getName
argument_list|()
operator|==
literal|null
condition|)
block|{
name|tempDiskUsage
operator|.
name|setName
argument_list|(
name|this
operator|.
name|tempUsage
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|tempDiskUsage
operator|.
name|setParent
argument_list|(
name|parent
operator|.
name|tempUsage
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|tempUsage
operator|=
name|tempDiskUsage
expr_stmt|;
block|}
block|}
end_class

end_unit

