begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * $Header$  *  * Broker Office ESPECIAL (Release) - org.apache.activemq.ra  *  * Copyright (C) 2005-2007 Norvax, Inc.  * All Rights Reserved  *  * This is UNPUBLISHED PROPRIETARY SOURCE CODE of Norvax, Inc.; the contents  * of this file may not be disclosed to third parties, copied or duplicated  * in any form, in whole or in part, without the prior written permission of  * Norvax, Inc. The copyright notice above does not evidence any actual or  * intended publication of such source code.  *   * Permission is hereby granted solely to the licensee for use of this source  * code in its unaltered state. This source code may not be modified by  * licensee except under direction of Norvax, Inc. Neither may this source  * code be given under any circumstances to non-licensees in any form,  * including source or binary. Modification of this source constitutes breach  * of contract, which voids any potential pending support responsibilities by  * Norvax, Inc. Divulging the exact or paraphrased contents of this source  * code to unlicensed parties either directly or indirectly constitutes  * violation of federal and international copyright and trade secret laws, and  * will be duly prosecuted to the fullest extent permitted under law.  *   * This software is provided by Norvax, Inc. ``as is'' and any express or  * implied warranties, including, but not limited to, the implied warranties  * of merchantability and fitness for a particular purpose are disclaimed. In  * no event shall the regents or contributors be liable for any direct,  * indirect, incidental, special, exemplary, or consequential damages  * (including, but not limited to, procurement of substitute goods or  * services; loss of use, data, or profits; or business interruption) however  * caused and on any theory of liability, whether in contract, strict  * liability, or tort (including negligence or otherwise) arising in any way  * out of the use of this software, even if advised of the possibility of such  * damage.  *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ra
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|ActivationSpec
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
name|RedeliveryPolicy
import|;
end_import

begin_comment
comment|/**  * Description: Description goes here.  *   * @author<a href="mailto:cstach@norvax.com">Christopher G. Stach II</a>  * @version $Revision$ $Date$  * @since 0.1  */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageActivationSpec
extends|extends
name|ActivationSpec
block|{
comment|/**      */
name|String
name|getClientId
parameter_list|()
function_decl|;
comment|/**      */
name|boolean
name|isDurableSubscription
parameter_list|()
function_decl|;
comment|/**      */
name|String
name|getPassword
parameter_list|()
function_decl|;
comment|/**      */
name|String
name|getUserName
parameter_list|()
function_decl|;
comment|/**      */
name|RedeliveryPolicy
name|redeliveryPolicy
parameter_list|()
function_decl|;
comment|/**      */
name|String
name|getSubscriptionName
parameter_list|()
function_decl|;
comment|/**      */
name|String
name|getMessageSelector
parameter_list|()
function_decl|;
comment|/**      */
name|int
name|getMaxMessagesPerSessionsIntValue
parameter_list|()
function_decl|;
comment|/**      */
name|boolean
name|getNoLocalBooleanValue
parameter_list|()
function_decl|;
comment|/**      */
name|String
name|getDestinationType
parameter_list|()
function_decl|;
comment|/**      */
name|String
name|getDestination
parameter_list|()
function_decl|;
comment|/** 	 */
name|int
name|getMaxSessionsIntValue
parameter_list|()
function_decl|;
comment|/**     *      */
name|int
name|getAcknowledgeModeForSession
parameter_list|()
function_decl|;
comment|/**      *      */
name|String
name|getMaxMessagesPerSessions
parameter_list|()
function_decl|;
comment|/**      *      */
name|boolean
name|isUseRAManagedTransactionEnabled
parameter_list|()
function_decl|;
comment|/**      *      */
name|String
name|getEnableBatch
parameter_list|()
function_decl|;
comment|/**      *      */
name|boolean
name|getEnableBatchBooleanValue
parameter_list|()
function_decl|;
comment|/**      *      */
name|int
name|getMaxMessagesPerBatchIntValue
parameter_list|()
function_decl|;
comment|/**      *      */
name|String
name|getMaxMessagesPerBatch
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

