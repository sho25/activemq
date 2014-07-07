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
name|kahadb
operator|.
name|scheduler
operator|.
name|legacy
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|index
operator|.
name|BTreeIndex
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
name|kahadb
operator|.
name|disk
operator|.
name|journal
operator|.
name|Location
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
name|kahadb
operator|.
name|disk
operator|.
name|page
operator|.
name|Transaction
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
name|kahadb
operator|.
name|disk
operator|.
name|util
operator|.
name|LongMarshaller
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
name|kahadb
operator|.
name|disk
operator|.
name|util
operator|.
name|VariableMarshaller
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
name|ByteSequence
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
name|ServiceStopper
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
name|ServiceSupport
import|;
end_import

begin_comment
comment|/**  * Read-only view of a stored legacy JobScheduler instance.  */
end_comment

begin_class
specifier|final
class|class
name|LegacyJobSchedulerImpl
extends|extends
name|ServiceSupport
block|{
specifier|private
specifier|final
name|LegacyJobSchedulerStoreImpl
name|store
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|BTreeIndex
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
argument_list|>
name|index
decl_stmt|;
name|LegacyJobSchedulerImpl
parameter_list|(
name|LegacyJobSchedulerStoreImpl
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
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
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
comment|/**      * Returns the next time that a job would be scheduled to run.      *      * @return time of next scheduled job to run.      *      * @throws IOException if an error occurs while fetching the time.      */
specifier|public
name|long
name|getNextScheduleTime
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
argument_list|>
name|first
init|=
name|this
operator|.
name|index
operator|.
name|getFirst
argument_list|(
name|this
operator|.
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|tx
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|first
operator|!=
literal|null
condition|?
name|first
operator|.
name|getKey
argument_list|()
else|:
operator|-
literal|1l
return|;
block|}
comment|/**      * Gets the list of the next batch of scheduled jobs in the store.      *      * @return a list of the next jobs that will run.      *      * @throws IOException if an error occurs while fetching the jobs list.      */
specifier|public
name|List
argument_list|<
name|LegacyJobImpl
argument_list|>
name|getNextScheduleJobs
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|LegacyJobImpl
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|LegacyJobImpl
argument_list|>
argument_list|()
decl_stmt|;
name|this
operator|.
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|tx
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Transaction
operator|.
name|Closure
argument_list|<
name|IOException
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
argument_list|>
name|first
init|=
name|index
operator|.
name|getFirst
argument_list|(
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|tx
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|LegacyJobLocation
name|jl
range|:
name|first
operator|.
name|getValue
argument_list|()
control|)
block|{
name|ByteSequence
name|bs
init|=
name|getPayload
argument_list|(
name|jl
operator|.
name|getLocation
argument_list|()
argument_list|)
decl_stmt|;
name|LegacyJobImpl
name|job
init|=
operator|new
name|LegacyJobImpl
argument_list|(
name|jl
argument_list|,
name|bs
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Gets a list of all scheduled jobs in this store.      *      * @return a list of all the currently scheduled jobs in this store.      *      * @throws IOException if an error occurs while fetching the list of jobs.      */
specifier|public
name|List
argument_list|<
name|LegacyJobImpl
argument_list|>
name|getAllJobs
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|LegacyJobImpl
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|LegacyJobImpl
argument_list|>
argument_list|()
decl_stmt|;
name|this
operator|.
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|tx
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Transaction
operator|.
name|Closure
argument_list|<
name|IOException
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
argument_list|>
argument_list|>
name|iter
init|=
name|index
operator|.
name|iterator
argument_list|(
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|tx
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
argument_list|>
name|next
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|LegacyJobLocation
name|jl
range|:
name|next
operator|.
name|getValue
argument_list|()
control|)
block|{
name|ByteSequence
name|bs
init|=
name|getPayload
argument_list|(
name|jl
operator|.
name|getLocation
argument_list|()
argument_list|)
decl_stmt|;
name|LegacyJobImpl
name|job
init|=
operator|new
name|LegacyJobImpl
argument_list|(
name|jl
argument_list|,
name|bs
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Gets a list of all scheduled jobs that exist between the given start and end time.      *      * @param start      *      The start time to look for scheduled jobs.      * @param finish      *      The end time to stop looking for scheduled jobs.      *      * @return a list of all scheduled jobs that would run between the given start and end time.      *      * @throws IOException if an error occurs while fetching the list of jobs.      */
specifier|public
name|List
argument_list|<
name|LegacyJobImpl
argument_list|>
name|getAllJobs
parameter_list|(
specifier|final
name|long
name|start
parameter_list|,
specifier|final
name|long
name|finish
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|LegacyJobImpl
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|LegacyJobImpl
argument_list|>
argument_list|()
decl_stmt|;
name|this
operator|.
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|tx
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Transaction
operator|.
name|Closure
argument_list|<
name|IOException
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
argument_list|>
argument_list|>
name|iter
init|=
name|index
operator|.
name|iterator
argument_list|(
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|tx
argument_list|()
argument_list|,
name|start
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
argument_list|>
name|next
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
operator|&&
name|next
operator|.
name|getKey
argument_list|()
operator|.
name|longValue
argument_list|()
operator|<=
name|finish
condition|)
block|{
for|for
control|(
name|LegacyJobLocation
name|jl
range|:
name|next
operator|.
name|getValue
argument_list|()
control|)
block|{
name|ByteSequence
name|bs
init|=
name|getPayload
argument_list|(
name|jl
operator|.
name|getLocation
argument_list|()
argument_list|)
decl_stmt|;
name|LegacyJobImpl
name|job
init|=
operator|new
name|LegacyJobImpl
argument_list|(
name|jl
argument_list|,
name|bs
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
name|ByteSequence
name|getPayload
parameter_list|(
name|Location
name|location
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|IOException
block|{
return|return
name|this
operator|.
name|store
operator|.
name|getPayload
argument_list|(
name|location
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"LegacyJobScheduler: "
operator|+
name|this
operator|.
name|name
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{     }
name|void
name|createIndexes
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|index
operator|=
operator|new
name|BTreeIndex
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
argument_list|>
argument_list|(
name|this
operator|.
name|store
operator|.
name|getPageFile
argument_list|()
argument_list|,
name|tx
operator|.
name|allocate
argument_list|()
operator|.
name|getPageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|void
name|load
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|index
operator|.
name|setKeyMarshaller
argument_list|(
name|LongMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|.
name|setValueMarshaller
argument_list|(
name|ValueMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|.
name|load
argument_list|(
name|tx
argument_list|)
expr_stmt|;
block|}
name|void
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|name
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|this
operator|.
name|index
operator|=
operator|new
name|BTreeIndex
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
argument_list|>
argument_list|(
name|this
operator|.
name|store
operator|.
name|getPageFile
argument_list|()
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|.
name|setKeyMarshaller
argument_list|(
name|LongMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|.
name|setValueMarshaller
argument_list|(
name|ValueMarshaller
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|this
operator|.
name|index
operator|.
name|getPageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|static
class|class
name|ValueMarshaller
extends|extends
name|VariableMarshaller
argument_list|<
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
argument_list|>
block|{
specifier|static
name|ValueMarshaller
name|INSTANCE
init|=
operator|new
name|ValueMarshaller
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
name|readPayload
parameter_list|(
name|DataInput
name|dataIn
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|LegacyJobLocation
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|dataIn
operator|.
name|readInt
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|LegacyJobLocation
name|jobLocation
init|=
operator|new
name|LegacyJobLocation
argument_list|()
decl_stmt|;
name|jobLocation
operator|.
name|readExternal
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|jobLocation
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writePayload
parameter_list|(
name|List
argument_list|<
name|LegacyJobLocation
argument_list|>
name|value
parameter_list|,
name|DataOutput
name|dataOut
parameter_list|)
throws|throws
name|IOException
block|{
name|dataOut
operator|.
name|writeInt
argument_list|(
name|value
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|LegacyJobLocation
name|jobLocation
range|:
name|value
control|)
block|{
name|jobLocation
operator|.
name|writeExternal
argument_list|(
name|dataOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

