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
name|leveldb
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
name|jmx
operator|.
name|MBeanInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  *<p>  *</p>  *  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|LevelDBStoreViewMBean
block|{
annotation|@
name|MBeanInfo
argument_list|(
literal|"The directory holding the store index data."
argument_list|)
name|String
name|getIndexDirectory
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The directory holding the store log data."
argument_list|)
name|String
name|getLogDirectory
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The size the log files are allowed to grow to."
argument_list|)
name|long
name|getLogSize
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The implementation of the LevelDB index being used."
argument_list|)
name|String
name|getIndexFactory
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Are writes synced to disk."
argument_list|)
name|boolean
name|getSync
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Is data verified against checksums as it's loaded back from disk."
argument_list|)
name|boolean
name|getVerifyChecksums
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The maximum number of open files the index will open at one time."
argument_list|)
name|int
name|getIndexMaxOpenFiles
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of keys between restart points for delta encoding of keys in the index"
argument_list|)
name|int
name|getIndexBlockRestartInterval
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Do aggressive checking of store data"
argument_list|)
name|boolean
name|getParanoidChecks
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Amount of data to build up in memory for the index before converting to a sorted on-disk file."
argument_list|)
name|int
name|getIndexWriteBufferSize
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Approximate size of user data packed per block for the index"
argument_list|)
name|int
name|getIndexBlockSize
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The type of compression to use for the index"
argument_list|)
name|String
name|getIndexCompression
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The size of the cache index"
argument_list|)
name|long
name|getIndexCacheSize
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The maximum amount of async writes to buffer up"
argument_list|)
name|int
name|getAsyncBufferSize
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The number of units of work which have been closed."
argument_list|)
name|long
name|getUowClosedCounter
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The number of units of work which have been canceled."
argument_list|)
name|long
name|getUowCanceledCounter
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The number of units of work which started getting stored."
argument_list|)
name|long
name|getUowStoringCounter
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The number of units of work which completed getting stored"
argument_list|)
name|long
name|getUowStoredCounter
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets and resets the maximum time (in ms) a unit of work took to complete."
argument_list|)
name|double
name|resetUowMaxCompleteLatency
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets and resets the maximum time (in ms) an index write batch took to execute."
argument_list|)
name|double
name|resetMaxIndexWriteLatency
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets and resets the maximum time (in ms) a log write took to execute (includes the index write latency)."
argument_list|)
name|double
name|resetMaxLogWriteLatency
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets and resets the maximum time (in ms) a log flush took to execute."
argument_list|)
name|double
name|resetMaxLogFlushLatency
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets and resets the maximum time (in ms) a log rotation took to perform."
argument_list|)
name|double
name|resetMaxLogRotateLatency
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets the maximum time (in ms) a unit of work took to complete."
argument_list|)
name|double
name|getUowMaxCompleteLatency
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets the maximum time (in ms) an index write batch took to execute."
argument_list|)
name|double
name|getMaxIndexWriteLatency
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets the maximum time (in ms) a log write took to execute (includes the index write latency)."
argument_list|)
name|double
name|getMaxLogWriteLatency
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets the maximum time (in ms) a log flush took to execute."
argument_list|)
name|double
name|getMaxLogFlushLatency
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets the maximum time (in ms) a log rotation took to perform."
argument_list|)
name|double
name|getMaxLogRotateLatency
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Gets the index statistics."
argument_list|)
name|String
name|getIndexStats
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Compacts disk usage"
argument_list|)
name|void
name|compact
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

