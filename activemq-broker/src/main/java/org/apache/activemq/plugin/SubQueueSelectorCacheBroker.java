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
name|plugin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
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
name|ConcurrentHashMap
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
name|broker
operator|.
name|Broker
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
name|broker
operator|.
name|BrokerFilter
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
name|broker
operator|.
name|ConnectionContext
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
name|broker
operator|.
name|region
operator|.
name|Subscription
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
name|ConsumerInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A plugin which allows the caching of the selector from a subscription queue.  *<p/>  * This stops the build-up of unwanted messages, especially when consumers may  * disconnect from time to time when using virtual destinations.  *<p/>  * This is influenced by code snippets developed by Maciej Rakowicz  *  * @author Roelof Naude roelof(dot)naude(at)gmail.com  * @see https://issues.apache.org/activemq/browse/AMQ-3004  * @see http://mail-archives.apache.org/mod_mbox/activemq-users/201011.mbox/%3C8A013711-2613-450A-A487-379E784AF1D6@homeaway.co.uk%3E  */
end_comment

begin_class
specifier|public
class|class
name|SubQueueSelectorCacheBroker
extends|extends
name|BrokerFilter
implements|implements
name|Runnable
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SubQueueSelectorCacheBroker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * The subscription's selector cache. We cache compiled expressions keyed      * by the target destination.      */
specifier|private
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|subSelectorCache
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|File
name|persistFile
decl_stmt|;
specifier|private
name|boolean
name|running
init|=
literal|true
decl_stmt|;
specifier|private
name|Thread
name|persistThread
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|MAX_PERSIST_INTERVAL
init|=
literal|600000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SELECTOR_CACHE_PERSIST_THREAD_NAME
init|=
literal|"SelectorCachePersistThread"
decl_stmt|;
comment|/**      * Constructor      */
specifier|public
name|SubQueueSelectorCacheBroker
parameter_list|(
name|Broker
name|next
parameter_list|,
specifier|final
name|File
name|persistFile
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|persistFile
operator|=
name|persistFile
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using persisted selector cache from["
operator|+
name|persistFile
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|readCache
argument_list|()
expr_stmt|;
name|persistThread
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|,
name|SELECTOR_CACHE_PERSIST_THREAD_NAME
argument_list|)
expr_stmt|;
name|persistThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|running
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|persistThread
operator|!=
literal|null
condition|)
block|{
name|persistThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|persistThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
comment|//if
block|}
annotation|@
name|Override
specifier|public
name|Subscription
name|addConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Caching consumer selector ["
operator|+
name|info
operator|.
name|getSelector
argument_list|()
operator|+
literal|"] on a "
operator|+
name|info
operator|.
name|getDestination
argument_list|()
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|selector
init|=
name|info
operator|.
name|getSelector
argument_list|()
decl_stmt|;
comment|// As ConcurrentHashMap doesn't support null values, use always true expression
if|if
condition|(
name|selector
operator|==
literal|null
condition|)
block|{
name|selector
operator|=
literal|"TRUE"
expr_stmt|;
block|}
name|subSelectorCache
operator|.
name|put
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
operator|.
name|getQualifiedName
argument_list|()
argument_list|,
name|selector
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
return|;
block|}
specifier|private
name|void
name|readCache
parameter_list|()
block|{
if|if
condition|(
name|persistFile
operator|!=
literal|null
operator|&&
name|persistFile
operator|.
name|exists
argument_list|()
condition|)
block|{
try|try
block|{
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|persistFile
argument_list|)
decl_stmt|;
try|try
block|{
name|ObjectInputStream
name|in
init|=
operator|new
name|ObjectInputStream
argument_list|(
name|fis
argument_list|)
decl_stmt|;
try|try
block|{
name|subSelectorCache
operator|=
operator|(
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|in
operator|.
name|readObject
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid selector cache data found. Please remove file."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//try
block|}
finally|finally
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//try
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to read persisted selector cache...it will be ignored!"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
comment|//try
block|}
comment|//if
block|}
comment|/**      * Persist the selector cache.      */
specifier|private
name|void
name|persistCache
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Persisting selector cache...."
argument_list|)
expr_stmt|;
try|try
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|persistFile
argument_list|)
decl_stmt|;
try|try
block|{
name|ObjectOutputStream
name|out
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|fos
argument_list|)
decl_stmt|;
try|try
block|{
name|out
operator|.
name|writeObject
argument_list|(
name|subSelectorCache
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//try
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to persist selector cache"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//try
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to access file["
operator|+
name|persistFile
operator|+
literal|"]"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
comment|//try
block|}
comment|/**      * @return The JMS selector for the specified {@code destination}      */
specifier|public
name|String
name|getSelector
parameter_list|(
specifier|final
name|String
name|destination
parameter_list|)
block|{
return|return
name|subSelectorCache
operator|.
name|get
argument_list|(
name|destination
argument_list|)
return|;
block|}
comment|/**      * Persist the selector cache every {@code MAX_PERSIST_INTERVAL}ms.      *      * @see java.lang.Runnable#run()      */
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|running
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|MAX_PERSIST_INTERVAL
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{             }
comment|//try
name|persistCache
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

