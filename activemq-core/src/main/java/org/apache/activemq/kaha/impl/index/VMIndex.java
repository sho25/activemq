begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|impl
operator|.
name|index
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|Marshaller
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
name|StoreEntry
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
name|impl
operator|.
name|container
operator|.
name|MapContainerImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * Index implementation using a HashMap  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|VMIndex
implements|implements
name|Index
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|VMIndex
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|IndexManager
name|indexManager
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Object
argument_list|,
name|StoreEntry
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|StoreEntry
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|VMIndex
parameter_list|(
name|IndexManager
name|manager
parameter_list|)
block|{
name|this
operator|.
name|indexManager
operator|=
name|manager
expr_stmt|;
block|}
comment|/**      *       * @see org.apache.activemq.kaha.impl.index.Index#clear()      */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param key      * @return true if the index contains the key      * @see org.apache.activemq.kaha.impl.index.Index#containsKey(java.lang.Object)      */
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * @param key      * @return store entry      * @see org.apache.activemq.kaha.impl.index.Index#removeKey(java.lang.Object)      */
specifier|public
name|StoreEntry
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|StoreEntry
name|result
init|=
name|map
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|result
operator|=
name|indexManager
operator|.
name|refreshIndex
argument_list|(
operator|(
name|IndexItem
operator|)
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to refresh entry"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to refresh entry"
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * @param key      * @param entry      * @see org.apache.activemq.kaha.impl.index.Index#store(java.lang.Object,      *      org.apache.activemq.kaha.impl.index.IndexItem)      */
specifier|public
name|void
name|store
parameter_list|(
name|Object
name|key
parameter_list|,
name|StoreEntry
name|entry
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param key      * @return the entry      */
specifier|public
name|StoreEntry
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|StoreEntry
name|result
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|result
operator|=
name|indexManager
operator|.
name|refreshIndex
argument_list|(
operator|(
name|IndexItem
operator|)
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to refresh entry"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to refresh entry"
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * @return true if the index is transient      */
specifier|public
name|boolean
name|isTransient
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * load indexes      */
specifier|public
name|void
name|load
parameter_list|()
block|{     }
comment|/**      * unload indexes      */
specifier|public
name|void
name|unload
parameter_list|()
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setKeyMarshaller
parameter_list|(
name|Marshaller
name|marshaller
parameter_list|)
block|{     }
block|}
end_class

end_unit

