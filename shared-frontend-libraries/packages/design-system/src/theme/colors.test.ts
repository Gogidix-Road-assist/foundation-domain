import { describe, it, expect } from 'vitest';
import { MANAGEMENT_COLORS, BUSINESS_DOMAIN_COLORS } from './colors';

describe('Color System', () => {
  describe('Management Colors', () => {
    it('should have all required management color properties', () => {
      expect(MANAGEMENT_COLORS).toHaveProperty('primary');
      expect(MANAGEMENT_COLORS).toHaveProperty('primaryHover');
      expect(MANAGEMENT_COLORS).toHaveProperty('primaryLight');
      expect(MANAGEMENT_COLORS).toHaveProperty('secondary');
      expect(MANAGEMENT_COLORS).toHaveProperty('secondaryLight');
      expect(MANAGEMENT_COLORS).toHaveProperty('accent');
      expect(MANAGEMENT_COLORS).toHaveProperty('accentHover');
    });

    it('should have correct management primary color', () => {
      expect(MANAGEMENT_COLORS.primary).toBe('#0066CC');
    });

    it('should have all functional colors', () => {
      expect(MANAGEMENT_COLORS).toHaveProperty('success');
      expect(MANAGEMENT_COLORS).toHaveProperty('warning');
      expect(MANAGEMENT_COLORS).toHaveProperty('error');
      expect(MANAGEMENT_COLORS).toHaveProperty('info');
    });
  });

  describe('Business Domain Colors', () => {
    it('should have all 7 business domains', () => {
      expect(Object.keys(BUSINESS_DOMAIN_COLORS)).toHaveLength(7);
    });

    it('should have Individual Insurance domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.individualInsurance).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.individualInsurance.primary).toBe('#6366F1');
    });

    it('should have Corporate Insurance domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.corporateInsurance).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.corporateInsurance.primary).toBe('#0D9488');
    });

    it('should have Insurance Core domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.insuranceCore).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.insuranceCore.primary).toBe('#4338CA');
    });

    it('should have Claims Automation domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.claimsAutomation).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.claimsAutomation.primary).toBe('#DC2626');
    });

    it('should have Mechanics domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.mechanics).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.mechanics.primary).toBe('#059669');
    });

    it('should have Partners Towing domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.partnersTowing).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.partnersTowing.primary).toBe('#D97706');
    });

    it('should have Vendors Ecommerce domain', () => {
      expect(BUSINESS_DOMAIN_COLORS.vendorsEcommerce).toBeDefined();
      expect(BUSINESS_DOMAIN_COLORS.vendorsEcommerce.primary).toBe('#7C3AED');
    });
  });
});
