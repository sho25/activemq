begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activecluster
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  * A Factory of Cluster instances  *   * @version $Revision: 1.3 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|ClusterFactory
block|{
comment|/**      * Creates a new cluster connection using the given  local name and destination name      * @param localName       * @param destinationName       *      * @return Cluster      * @throws JMSException      */
specifier|public
name|Cluster
name|createCluster
parameter_list|(
name|String
name|localName
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a new cluster connection using the given  local name and destination name      * @param localName       * @param destinationName       * @param marshaller       *      * @return Cluster      * @throws JMSException      */
specifier|public
name|Cluster
name|createCluster
parameter_list|(
name|String
name|localName
parameter_list|,
name|String
name|destinationName
parameter_list|,
name|DestinationMarshaller
name|marshaller
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a new cluster connection - generating the localName automatically      * @param destinationName      * @return the Cluster      * @throws JMSException      */
specifier|public
name|Cluster
name|createCluster
parameter_list|(
name|String
name|destinationName
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a new cluster connection using the given  local name and destination name      * @param localName       * @param destination      *      * @return Cluster      * @throws JMSException      */
specifier|public
name|Cluster
name|createCluster
parameter_list|(
name|String
name|localName
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a new cluster connection using the given  local name and destination name      * @param localName       * @param destination      * @param marshaller       *      * @return Cluster      * @throws JMSException      */
specifier|public
name|Cluster
name|createCluster
parameter_list|(
name|String
name|localName
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|DestinationMarshaller
name|marshaller
parameter_list|)
throws|throws
name|JMSException
function_decl|;
comment|/**      * Creates a new cluster connection - generating the localName automatically      * @param destination      * @return the Cluster      * @throws JMSException      */
specifier|public
name|Cluster
name|createCluster
parameter_list|(
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
function_decl|;
block|}
end_interface

end_unit

